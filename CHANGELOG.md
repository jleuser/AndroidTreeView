# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [Unreleased]
### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security


## [v1.4.0] - 2017-12-03
First version after fork of original AndroidTreeView.

**Changes TBD**


## [v1.3.x] - Not released
Version was not released as only used in a different fork of AndroidTreeView.

----

> The following releases are a best guess of the history of the original AndroidTreeView. Because there are no tags in the repository, the dates of the releases might be a couple of days off (because sometimes it is not clear whether a release was actually defined when the version code was changed or whether additional changes were part of the version)

## [v1.2.9] - 2016-05-03
Latest released version of original AndroidTreeView before fork.

*no code changes*


## [v1.2.8] - 2016-04-25
### Added
*   Allow deactivating auto-toggling mode of tree nodes

### Changed
*   Expose some fields of AndroidTreeView-class to subclasses


## [v1.2.7] - 2015-11-05
### Changed
*   Removed allowBackup in library, see bmelnychuk/AndroidTreeView#38


## [v1.2.6] - 2015-09-24
### Added
*   Added click and long click listener

### Changed
*   Node IDs get generated differently


## [v1.2.5] - 2015-06-02
### Added
*   2D scrolling mode  
    *Keep in mind this comes with few limitations: you won't be able not place views on right side like alignParentRight. Everything should be align left. Is not enabled by default*
*   Background of selected items coupled to ```?android:attr/selectableItemBackground```


## [v1.2.4] - 2015-04-11
### Fixed
*   Different padding issues: bmelnychuk/AndroidTreeView#1, bmelnychuk/AndroidTreeView#11 [Padding issues](https://github.com/bmelnychuk/AndroidTreeView/issues/1), [Padding left on android < 5.0](https://github.com/bmelnychuk/AndroidTreeView/issues/11)


## [v1.2.3] - 2015-03-21
### Fixed
*   [Restore state issue](https://github.com/bmelnychuk/AndroidTreeView/issues/7)


## [v1.2.2] - 2015-03-13
### Added
*   Animation for expand and collapse


## [v1.2.1] - 2015-02-19
### Fixed
*   Fire listener before collapsing/expanding


## [v1.2.0] - 2015-02-18
### Added
*   Dynamic addition/removal of tree nodes


## [v1.1.0] - 2015-02-16
### Added
*   Selection mode for nodes


## v1.0.0 - 2015-02-14
Initial version


[Unreleased]: https://github.com/jleuser/AndroidTreeView/compare/v1.4.0...HEAD
[v1.4.0]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.9...v1.4.0
[v1.2.9]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.8...v1.2.9
[v1.2.8]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.7...v1.2.8
[v1.2.7]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.6...v1.2.7
[v1.2.6]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.5...v1.2.6
[v1.2.5]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.4...v1.2.5
[v1.2.4]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.3...v1.2.4
[v1.2.3]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.2...v1.2.3
[v1.2.2]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.1...v1.2.2
[v1.2.1]: https://github.com/jleuser/AndroidTreeView/compare/v1.2.0...v1.2.1
[v1.2.0]: https://github.com/jleuser/AndroidTreeView/compare/v1.1.0...v1.2.0
[v1.1.0]: https://github.com/jleuser/AndroidTreeView/compare/v1.0.0...v1.1.0
