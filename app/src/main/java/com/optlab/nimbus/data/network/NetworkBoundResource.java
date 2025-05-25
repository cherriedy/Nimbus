package com.optlab.nimbus.data.network;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A generic class that implements the Network Bound Resource pattern. It loads data from the local
 * database first, then decides whether to fetch from the network. If network fetch is needed, it
 * saves the result to the local database and emits the updated data.
 */
public abstract class NetworkBoundResource<RequestType, ResultType> {
    /**
     * Returns an Observable that emits data from local, and optionally from remote if needed.
     */
    public Observable<ResultType> asObservable() {
        return getFromLocal()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(this::fetchAndCache)
                .doOnComplete(() -> Timber.d("Success fetching data"))
                .doOnError(e -> Timber.e("Error fetching data: %s", e.getMessage()));
    }

    private Observable<ResultType> fetchAndCache(ResultType cachedData) {
        if (fetchIfNecessary(cachedData)) {
            return getFromRemote()
                    .subscribeOn(Schedulers.io())
                    .doOnNext(this::saveRemoteResult)
                    .flatMap(response -> getFromLocal())
                    .onErrorResumeNext(
                            e -> {
                                Timber.e(e, "Remote fetch failed, falling back to local data");
                                return getFromLocal();
                            });
        } else {
            return Observable.just(cachedData);
        }
    }

    /**
     * Determines whether to fetch data from the network based on the current local data.
     */
    protected abstract boolean fetchIfNecessary(ResultType localData);

    /**
     * Loads data from the local database.
     */
    protected abstract Observable<ResultType> getFromLocal();

    /**
     * Fetches data from the remote source (network).
     */
    protected abstract Observable<RequestType> getFromRemote();

    /**
     * Saves the result from the remote source to the local database.
     */
    protected abstract void saveRemoteResult(RequestType item);
}
