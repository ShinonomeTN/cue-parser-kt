# CUE File Parser in Kotlin

Simple CUE Sheet parser implement in Kotlin
    
## Usage

### Parse CUE content

```kotlin
fun main() {
    println("Read CUE sheet file.")
    val cue = CueParser(FileInputStream(File("sample.cue")))
    println(cue)
}
```
    
### Read Album information using provided reader

```kotlin
fun main() {
    val reader = CueParser(FileInputStream(File("sample.cue"))).reader()
    println("""
        Cue file information:
        ------------------------------------------------
        Title    : ${reader.albumInfo(AlbumInfo.Title)}
        Performer: ${reader.albumInfo(AlbumInfo.Performer)}
        Genre    : ${reader.albumInfo(AlbumInfo.Genre)}
        Date     : ${reader.albumInfo(AlbumInfo.Date)}
        ------------------------------------------------
        Raw data:
        ------------------------------------------------
        ${reader.rootNode.properties}
    """.trimIndent())
}
```

Current supported meta:

- Catalog
- CDTextFile
- Performer
- SongWriter
- Title
- DiscID
- Genre
- Date

### Read track info

```kotlin
fun main() {
    val reader = CueParser(FileInputStream(File("sample.cue"))).reader()
    val file = reader.mediaFile()
    val trackList = file.trackList()

    trackList.forEach {
        println("""
            Track ${it.number} Info:
            ---------------------------------------
            Type     :${it.type}
            Title    : ${it.trackInfo(TrackInfo.Title)}
            Performer: ${it.trackInfo(TrackInfo.Performer)}
            ---------------------------------------
            Raw data :
            ${it.node.properties}
            ---------------------------------------
        """.trimIndent())
    }
}
```

Current supported meta:

- Title
- Performer
- SongWriter
- ISRC
- Number
- Year
- Genre

## Details: CueParser

There are two CueParser usage:
1. CueParser() function accept an InputStream, return root node of CUE info tree. See above.
2. CueParser() constructor accept an Iterable of CUE content lines, creates an iterator.

The hierarchy of a CUE sheet usually like this:

```
- meta
- file
  |- track
     |- meta
```

CUE Sheets that not in this hierarchy will cause errors when parsing. 

### CueNode

Each CueNode has those fields:
- type: node type
- parent: parent node, root has no parent
- properties: a Map containing meta info about this node
- children: child nodes

There are two types of node can become parent:
- ROOT: the CUE Sheet itself, containing FILEs
- FILE: containing TRACKs

TRACK has no children.

(The parser supports multiple FILE declaration.)

## CueInfoReader

CueInfoReader is the default reader implementation provided, use `CueTreeNode.reader()` on root node to get one.

It uses `CueTreeNode.properties` to read album and track info.

# Reference
[CUE sheet format](https://github.com/libyal/libodraw/blob/master/documentation/CUE%20sheet%20format.asciidoc)