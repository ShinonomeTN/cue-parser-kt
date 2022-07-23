package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.MutableCueTreeNode

interface CueBaseBuilderContext {
    @CueBuilderDsl
    fun rem(vararg args : String) : MutableCueTreeNode

    @CueBuilderDsl
    fun comment(comment : String) : MutableCueTreeNode
}