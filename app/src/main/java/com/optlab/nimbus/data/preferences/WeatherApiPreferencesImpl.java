package com.optlab.nimbus.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.common.WeatherProvider;

import timber.log.Timber;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * SecurePrefsManager is a class that manages secure shared preferences using the Android Keystore
 *
 * <p>To store the data in the secure shared preferences, we need to encrypt the data using the
 * secret key. First, we need to check if the key exists in the Android Keystore. If it does not
 * exist, we need to generate a new key. Then, we can use the key to encrypt the data and store it
 * in the shared preferences.
 */
public class WeatherApiPreferencesImpl implements WeatherApiPreferences {
    /**
     * The name of the shared preferences file. This is the name used to create or access the shared
     */
    private static final String PREF_NAME = "secure_api_pref";

    /**
     * The alias for the key in the Android Keystore. This alias is used to retrieve the key from
     * the keystore.
     */
    private static final String KEY_STORE_ALIAS = "secure_api_key_alias";

    /** The Android Keystore system provides a container to store cryptographic keys in a secure */
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    /** AES/GCM/NoPadding is a secure encryption algorithm that provides confidentiality and */
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    /** The tag length is 128 bits (16 bytes) for GCM mode. This is the default size for GCM */
    private static final int TAG_LENGTH_BIT = 128;

    private final SharedPreferences securePrefs;

    public WeatherApiPreferencesImpl(Context context) {
        this.securePrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        generateKeyIfNeeded();
        initApiKey();
    }

    /**
     * @noinspection SpellCheckingInspection
     */
    private void initApiKey() {
        setApiKey(WeatherProvider.TOMORROW_IO.name(), "jxlIW6sxnnCUFCAIiCYPgnuckubOdW5s");
    }

    private void generateKeyIfNeeded() {
        try {
            // Load the key which is used to encrypt/decrypt the shared preferences.
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null); // Load the keystore from the AndroidKeyStore

            // If the key does not exist, generate a new one.
            if (!keyStore.containsAlias(KEY_STORE_ALIAS)) {
                // Parameters for the key generation specification.
                KeyGenParameterSpec keyGenParameterSpec =
                        new KeyGenParameterSpec.Builder(
                                        KEY_STORE_ALIAS,
                                        KeyProperties.PURPOSE_ENCRYPT
                                                | KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setKeySize(256)
                                .build();

                // Generate the key with the specified parameters by using AES algorithm, and then
                // store in the Android Keystore ("AndroidKeyStore").
                KeyGenerator keyGenerator =
                        KeyGenerator.getInstance(
                                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
                keyGenerator.init(keyGenParameterSpec); // Initialize the key with the params
                keyGenerator.generateKey(); // Generate the new key
            }
        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException e) {
            Log.e("SecurePrefsManager", "Error generating key: " + e.getMessage());
        } catch (NoSuchProviderException e) {
            Log.e("SecurePrefsManager", "No such provider: " + e.getMessage());
        }
    }

    private SecretKey getSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null); // Load the keystore from the AndroidKeyStore
            // Retrieve the secret key from the keystore using the alias.
            return (SecretKey) keyStore.getKey(KEY_STORE_ALIAS, null);
        } catch (KeyStoreException
                | CertificateException
                | IOException
                | UnrecoverableKeyException
                | NoSuchAlgorithmException e) {
            Log.e("SecurePrefsManager", "Error loading keystore: " + e.getMessage());
            return null; // Return null if the key cannot be loaded
        }
    }

    /**
     * Get the API key for the specified provider from the secure shared preferences. The data is
     * decrypted using the secret key and IV stored in the shared preferences.
     */
    @Override
    public void setApiKey(String provider, String key) {
        saveEncrypted(provider, key);
    }

    /**
     * Get the API key for the specified endpoint from the secure shared preferences. The data is
     * decrypted using the secret key and IV stored in the shared preferences.
     */
    @Override
    public String getApiKey(String provider) {
        return getDecrypted(provider);
    }

    /**
     * Remove the API key for the specified provider from the secure shared preferences. The
     * encrypted data and IV are removed from the shared preferences.
     */
    @Override
    public void removeApiKey(String provider) {
        securePrefs.edit().remove(provider + "_iv").remove(provider).apply();
    }

    @Override
    public void registerOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        securePrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        securePrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Save the data in the secure shared preferences. The data is encrypted using the secret key
     * and stored in the shared preferences.
     *
     * <p>First, we generate a random IV (initialization vector) for the encryption. Then, we
     * initialize the cipher for encryption using the secret key and IV. After that, we encrypt the
     * data using the cipher, which returns the encrypted bytes. Finally, we encode the encrypted
     * data and IV to Base64 strings for storage. The encrypted data and IV are stored in the shared
     * preferences.
     *
     * @param key The key to store the data in the shared preferences.
     * @param value The value to store in the shared preferences.
     */
    private void saveEncrypted(String key, String value) {
        try {
            // Initialize the cipher for encryption using the secret key and IV.
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

            byte[] iv = cipher.getIV();

            // Encrypt the data using the cipher, which returns the encrypted bytes. The data is
            // converted to bytes using UTF-8 encoding.
            byte[] encryptedDataBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            // Encode the encrypted data and IV to Base64 strings for storage.
            String encryptedData = Base64.encodeToString(encryptedDataBytes, Base64.DEFAULT);
            String encodedIv = Base64.encodeToString(iv, Base64.DEFAULT);

            securePrefs // Store the encrypted data and IV in the shared preferences.
                    .edit() // Get the editor to edit the shared preferences.
                    .putString(key + "_iv", encodedIv) // Store the IV separately
                    .putString(key, encryptedData) // Store the encrypted data
                    .apply(); // Apply the changes to the shared preferences
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            Timber.e("Error initializing cipher: %s", e.getMessage());
        }
    }

    /**
     * Decrypt the data from the secure shared preferences. The data is decrypted using the secret
     * key and IV stored in the shared preferences.
     *
     * <p>First, we retrieve the encrypted data and IV from the shared preferences. Then, we decode
     * the Base64 strings to byte arrays. After that, we initialize the cipher for decryption using
     * the secret key and IV. Finally, we decrypt the data using the cipher, which returns the
     * decrypted bytes. We convert the decrypted bytes to a string and return it.
     *
     * @param key The key to retrieve the data from the shared preferences.
     * @return The decrypted data as a string, or null if the data cannot be decrypted.
     */
    private String getDecrypted(String key) {
        try {
            // Retrieve the encrypted data and IV from the shared preferences.
            String encryptedData = securePrefs.getString(key, null);
            String encodedIv = securePrefs.getString(key + "_iv", null);
            // If the encrypted data or IV is null, return null.
            if (encodedIv == null || encryptedData == null) return null;

            // Decode the Base64 strings to byte arrays.
            byte[] iv = Base64.decode(encodedIv, Base64.DEFAULT);
            byte[] encryptedDataBytes = Base64.decode(encryptedData, Base64.DEFAULT);

            // Initialize the cipher for decryption using the secret key and IV.
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            // The cipher is initialized in GCM mode with the specified tag length.
            cipher.init(
                    Cipher.DECRYPT_MODE, getSecretKey(), new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            // Decrypt the data using the cipher, which returns the decrypted bytes.
            byte[] decryptedDataBytes = cipher.doFinal(encryptedDataBytes);
            // Convert the decrypted bytes to a string using UTF-8 encoding.
            return new String(decryptedDataBytes, StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            Timber.e("Error initializing cipher: %s", e.getMessage());
            return null; // Return null if the data cannot be decrypted
        }
    }
}
