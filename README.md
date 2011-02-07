sked
====

sked contains java code related to scheduling

The project is hosted in maven central.
[here](https://repository.sonatype.org/index.html#nexus-search;quick~sked) you'll find a quick copy/paste for the dependency.


TODO
----

- Deploy javadocs in github pages


Changelog
---------

### sked-1.1 - Released 2011/02/07

- Added a CronScheduleFactory with convenience methods to ease cron expression generation
- CronSchedule instances are now Serializable.

### sked-1.1 - Released 2010/12/31

- Fixed a corner case bug in CronSchedule.firstRunAfter( timestamp ) when timestamp is less than a second ahead of previous run

### sked-1.0 - Released 2010/12/12

- Initial release, *do not use*, contains a bug fixed in 1.1

