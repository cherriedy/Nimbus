package com.optlab.nimbus.data.network;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    @SuppressWarnings("unchecked")
    public Flowable<ResultType> asFlowable() {
        return (Flowable<ResultType>)
                getFromLocal()
                        .doOnError(this::onLocalFetchError)
                        .doOnSuccess(this::onLocalFetchSuccess)
                        .flatMapPublisher(
                                data -> {
                                    if (shouldFetch(data)) {
                                        Timber.d("Should fetch from remote");
                                        return fetchFromRemoteFlowable();
                                    } else {
                                        Timber.d("Using local data, no remote fetch needed");
                                        return Flowable.just(data);
                                    }
                                })
                        .switchIfEmpty(fetchFromRemoteFlowable());
    }

    private Flowable<RequestType> fetchFromRemoteFlowable() {
        return fetchFromRemote().doOnSuccess(this::cacheFetchResult).toFlowable();
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
    protected abstract Maybe<ResultType> getFromLocal();

    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    protected abstract Single<RequestType> fetchFromRemote();

    protected abstract void cacheFetchResult(@NonNull RequestType item);
}
