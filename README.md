sked
====

sked contains java code related to scheduling.

Cron expression parsing is based on the GNU crontab manpage that can be found
here: http://unixhelp.ed.ac.uk/CGI/man-cgi?crontab+5
 
The following extensions are used:

- a mandatory field is added at the begining: seconds.
- a special string is added: @minutely
- a special character is added: ? to choose between dayOfMonth and dayOfWeek

The ? special char has the same behavior as in the Quartz Scheduler expression.
The wikipedia page http://en.wikipedia.org/wiki/CRON_expression explains Quartz
Scheduler expression, not simple cron expressions. You'll find there about the ?
special char and maybe that some other extensions you would like to use are missing
in this project.

The project is hosted in maven central.
[Here](https://repository.sonatype.org/index.html#nexus-search;quick~sked) you'll find a quick copy/paste for the dependency.


Changelog
---------

### sked-2.1 - Released 2014/01/12

- Made SLF4J dependency optional
- Starting with this version, sked has zero dependencies

### sked-2.0 - Released 2014/01/11

- Removed Joda-Time dependency, now using plain old java.util.Calendar only
- Changed package!
- Minor optimisations

### sked-1.2 - Released 2011/02/07

- Added a CronScheduleFactory with convenience methods to ease cron expression generation
- CronSchedule instances are now Serializable.

### sked-1.1 - Released 2010/12/31

- Fixed a corner case bug in CronSchedule.firstRunAfter( timestamp ) when timestamp is less than a second ahead of previous run

### sked-1.0 - Released 2010/12/12

- Initial release, *do not use*, contains a bug fixed in 1.1

