# IsoSax
ISO SAX is a callback-based parser for ISO container files (ISO/IEC 14496-12), e.g. MPEG-4.

The libraries that are out there either won't run on Android, have many megabytes of dependent JARs, or will fail to parse your favorite media file due to a "technicality" it thinks it is mal-formed.

For example, a perfectly good M4B file gets declared "invalid" because it had a video track in it (the album art), along with the sound track. Really!? Don't let these libraries "judge" the format and lock you out!

Then there's the obsession with representing every last byte of a media file, when all you want is the metadata.

# Features
* Cherry-pick the bits you want
* Not concerned with saving
* Android-safe!
* No dependencies
* Small and fast
* Not concerned with validating file type, etc.
* Don't want to know about "boxes"? Comes with common scenarios covered for you

# Maven Central
We are publishing Java packages to Maven Central; here is the info for this artifact:

* group: **com.escape-technology-llc**
* artifact: **IsoSax**
* version: **SNAPSHOT-1.0**
