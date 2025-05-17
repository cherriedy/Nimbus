package com.optlab.nimbus.data.network;

import android.annotation.SuppressLint;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    @SuppressWarnings("unchecked")
    public Flowable<ResultType> asFlowable() {
        return fromLocal()
                .doOnSuccess(this::onLocalFetchSuccess)
                .doOnError(this::onLocalFetchError)
                .flatMapPublisher(
                        cachedResponse -> {
                            if (shouldFetch(cachedResponse)) {
                                return fromRemoteAsFlowable();
                            }
                            return Flowable.just(cachedResponse);
                        })
                .switchIfEmpty(fromRemoteAsFlowable());
    }

    @SuppressLint("CheckResult")
    private Flowable<ResultType> fromRemoteAsFlowable() {
        fromRemote()
                .doOnSuccess(this::cacheRefreshing)
                .doOnError(
                        throwable -> {
                            Timber.e(throwable, "Error fetching from remote");
                        });
        return fromLocal().toFlowable();
    }

    private void onLocalFetchError(Throwable throwable) {
        Timber.e(throwable, "Error fetching from local");
    }

    private void onLocalFetchSuccess(ResultType resultType) {
        if (resultType == null) {
            Timber.d("Local data is null, fetching from remote");
        } else {
            Timber.d("Local data fetched successfully");
        }
    }

    @NonNull
    protected abstract Maybe<ResultType> fromLocal();

    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    protected abstract Single<RequestType> fromRemote();

    protected abstract void cacheRefreshing(@NonNull RequestType item);
}
