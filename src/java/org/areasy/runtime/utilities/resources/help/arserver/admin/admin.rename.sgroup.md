# Rename Support Group

This action is an ITSM utility, designed to rename a support group by making _Obsolete_ the original one and creating a new one with the specified details. All related details (e.g. aliases and memberships etc.) are copied to the new support group.
Rather than this the action is able to replace the old support group with the new one in incidents, problems, known errors, changes work orders and asset relationships.
Complementary, this action applies the specified group transformation to the configuration areas from foundation layer that refer support groups.

The standard command line is described below:

    areasy -action admin.rename.sgroup
    	-sgroupcompany <current support group company> -sgrouporganisation <current support group organisation> -sgroupname <current support group name>
    	-newsgroupcompany <new support group company> -newsgrouporganisation <new support group organisation> -newsgroupname <new support group name>
    	[-allgroupdetails]
    	[-members] [-functionalroles] [-aliases] [-favorites] [-oncalls] [-shifts]
    	[-allrelatedtickets]
    	[-incidenttemplates] [-incidents [-incidentsbefore <date> | -incidentsafter <date> | -openincidents]]
    	[-problemtemplates]	[-problems [-problemsbefore <date> | -problemsafter <date> | -openproblems]]
    	[-knownerrors [-knownerrorsbefore <date> | -knownerrorsafter <date> | -openknownerrors]]
    	[-changetemplates] [-changes [-changesbefore <date> | -changesafter <date> | -openchanges]]
    	[-tasktemplates] [-tasks [-tasksbefore <date> | -tasksafter <date> | -opentasks]]
    	[-workordertemplates] [-workorders [-workordersbefore <date> | -workorderafter <date> | -openworkorders]]
    	[-assetrelationships] [-cmdbrelationships] [-knowledgerecords]
    	[-approvalmappings]


__Details__

| Parameter Name          | Documentation     |
| ----------------------- | ----------------- |
| `sgroupcompany`         | current support group company. In addition the action supports another alias for this option: `supportgroupcompany`|
| `sgrouporganisation`    | current support group organisation. In addition the action supports another alias for this option: `supportgrouporganisation`|
| `sgroupname`            | current support group name. In addition the action supports some aliases for this option: `sgroup`, `supportgroup`, `supportgroupname`|
| `newsgroupcompany`      | new support group company (company part of the new support group that will be created). In addition the action supports another alias for this option: `newsupportgroupcompany`|
| `newsgrouporganisation` | new support group organisation (organisation part of the new support group that will be created). In addition the action supports another alias for this option: `newsupportgrouporganisation`|
| `newsgroupname`         | new support group name (group name part of the new support group that will be created). In addition the action supports some aliases for this option: `sgroup`, `supportgroup`, `supportgroupname`|
| `allgroupdetails`       | transfer all related details from old support group to the new support group. Here are considered all members, alias, functional roles, oncall records, shifts, etc. (actually this option could be used in case of you want to create a mirrored support group).|
| `members`               | transfer the people accounts that are members to old support group to the new support group. Together with this transfer are managed in the same way the functional roles|
| `keepoldmembers`        | |
| `functionalroles`       | |
| `keepoldfunctionalroles`| |
| `aliases`               | transfer old support group aliases to the new support group|
| `keepoldaiases`         | |
| `favorites`             | transfer old support group favorites to the new support group|
| `keepoldfavorites`      | |
| `oncalls`               | transfer old support group favorites to the new support group|
| `shifts`                | transfer old support group shifts to the new support group|
| `allrelatedtickets`     | change all assignment types in all type of ticket managed by ITSM suite (incidents, problems, known errors, changes and asset relationships). In case of you want to convert discrete ticket types you have use the options described below (for each ticket type)|
| `approvalmappings`      | transfer old support group approval mappings to the new support group|
| `incidenttemplates`     | change the assignment and all authoring support groups in incident templates|
| `incidents`             | change the assignment and owner support groups in incident tickets|
| `incidentsbefore`       | allows you to change only incidents that are created before the specified date. This option excludes 'incidentsafter' option and vice-versa|
| `incidentsafter`        | allows you to change only incidents that are created after the specified date. This option excludes 'incidentsbefore' option and vice-versa|
| `openincidents`         | allows you to change only open incidents (all incidents that have status = Assigned, In Progress, Pending or Resolved)|
| `problemtemplates`      | change the assignment and all authoring support groups in problem templates|
| `problems`              | change the assignment and coordinator support groups in problem tickets|
| `problemsbefore`        | allows you to change only problems that are created before the specified date. This option excludes 'problemsafter' option and vice-versa|
| `problemsafter`         | allows you to change only problems that are created after the specified date. This option excludes 'problemsbefore' option and vice-versa|
| `openproblems`          | allows you to change only open problems (all problems that have status = Draft, Under Review, Request For Authorization, Assigned, Under Investigation, Pending or Completed)|
| `knownerrors`           | change the assignment and coordinator support groups in known errors tickets|
| `knownerrorsbefore`     | allows you to change only known errors that are created before the specified date. This option excludes 'knownerrorssafter' option and vice-versa|
| `knownerrorssafter`     | allows you to change only known errors that are created after the specified date. This option excludes 'knownerrorsbefore' option and vice-versa|
| `openknownerrors`       | allows you to change only open known errors (all known errors that have status = Assigned, Scheduled For Correction, Assigned To Vendor, No Action Planned or Corrected)|
| `changetemplates`       | update the assignment and all authoring support groups in change templates|
| `changes`               | update the assignment, coordinator and change manager support groups in change tickets|
| `changesbefore`         | allows you to update only changes that are created before the specified date. This option excludes 'changesafter' option and vice-versa|
| `changesafter`          | allows you to update only changes that are created after the specified date. This option excludes 'changesbefore' option and vice-versa|
| `openchanges`           | allows you to update only open changes (all changes that have status = Draft, Request For Authorization, Request For Change, Planning In Progress, Scheduled For Review, Scheduled For Approval, Scheduled, Implementation In Progress, Pending, Rejected or Completed)|


__Examples__

    areasy -runtime -action admin.rename.sgroup -members -aliases -supportgroupcompany "Calbro Services" -supportgrouporganisation "Services Performance" -supportgroupname "Development" -newsupportgroupcompany "Calbro Services" -newsupportgrouporganisation "IT Service Management" -newsupportgroupname "Development" -loglevel info
= makes old support group offline, creates a new support group and transfers from old group to the new one the following entities: aliases, people accounts and application functional roles

    areasy -runtime -action admin.rename.sgroup -members -aliases -incidents -incidenttemplates -supportgroupcompany "Calbro Services" -supportgrouporganisation "Services Performance" -supportgroupname "Development" -newsupportgroupcompany "Calbro Services" -newsupportgrouporganisation "IT Service Management" -newsupportgroupname "Development" -loglevel info
= performs usual actions (mentioned in the first example) and additionally update changes in incidents and incident templates replacing old group with the new one and authoring areas is updated as well. All secondary authoring groups are evaluated for this type of change.

    areasy -runtime -action admin.rename.sgroup -allgroupdetails -supportgroupcompany "Calbro Services" -supportgrouporganisation "IT Support" -supportgroupname "Service Desk" -newsupportgroupcompany "Calbro Services" -newsupportgrouporganisation "IT Support" -newsupportgroupname "Help Desk" -loglevel info -assetrelationships on
= makes old group offline creates a new support group and transfer from old group to the new one all details. Additionally update all asset relationships where a support group is related, replacing old group with the new one.
