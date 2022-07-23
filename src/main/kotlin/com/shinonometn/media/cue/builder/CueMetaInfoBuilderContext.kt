package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.MutableCueTreeNode

interface CueMetaInfoBuilderContext : CueBaseBuilderContext {
    /**
     * Any not special meta info
     */
    @CueBuilderDsl
    fun meta(vararg args : String) : MutableCueTreeNode

}