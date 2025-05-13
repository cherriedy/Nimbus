package com.optlab.nimbus.data.common;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    public Flowable<ResultType> asFlowable() {
        return getFromLocal()
                .flatMap(
                        data -> {
                            if (shouldFetch(data)) {
                                return fetchFromRemote()
                                        .doOnSuccess(this::cacheFetchResult)
                                        .toFlowable()
                                        .flatMap(ignore -> getFromLocal());
                            } else {
                                return Flowable.just(data);
                            }
                        });
    }

    @NonNull
    protected abstract Flowable<ResultType> getFromLocal();

    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    protected abstract Single<RequestType> fetchFromRemote();

    protected abstract void cacheFetchResult(@NonNull RequestType item);
}
