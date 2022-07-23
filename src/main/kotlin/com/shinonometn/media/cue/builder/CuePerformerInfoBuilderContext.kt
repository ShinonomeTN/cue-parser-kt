package com.shinonometn.media.cue.builder

import com.shinonometn.media.cue.core.MutableCueTreeNode

interface CuePerformerInfoBuilderContext : CueMetaInfoBuilderContext {
    /**
     * Name(s) of the performer(s)
     * */
    fun performers(vararg name: String) : MutableCueTreeNode {
        return meta("PERFORMER", *name)
    }

    /**
     * Name(s) of the songwriter(s)
     */
    fun songwriter(vararg name: String) : MutableCueTreeNode {
        return meta("SONGWRITER", *name)
    }

    /**
     * Title of album name or Track Titles
     */
    fun title(name: String) : MutableCueTreeNode {
        return meta("TITLE", name)
    }

    /**
     * Name(s) of the arranger(s)
     */
    fun arranger(vararg name: String) : MutableCueTreeNode {
        return meta("ARRANGER", *name)
    }

    /**
     * Name(s) of the composer(s)
     */
    fun composer(vararg name: String) : MutableCueTreeNode {
        return meta("COMPOSER", *name)
    }
}