package com.shinonometn.media.cue

import java.util.stream.Collectors

internal infix fun <I, O> Collection<O>.indexToMap(indexProvider: (O) -> I): MutableMap<I, O> =
    stream().collect(Collectors.toMap(
        { i -> indexProvider(i) },
        { i -> i }
    ))