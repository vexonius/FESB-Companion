package com.tstudioz.fax.fme.feature.merlin.database
/*{
                    "id": "3307666",
                    "anchor": "module-3307666",
                    "name": "Prezentacije",
                    "visible": true,
                    "stealth": false,
                    "sectionid": "1982686",
                    "sectionnumber": 1,
                    "uservisible": true,
                    "hascmrestrictions": false,
                    "modname": "Mapa",
                    "indent": 0,
                    "module": "folder",
                    "plugin": "mod_folder",
                    "accessvisible": true,
                    "url": "https:\\\/\\\/moodle.srce.hr\\\/2023-2024\\\/mod\\\/folder\\\/view.php?id=3307666",
                    "istrackeduser": true,
                    "allowstealth": true
                }*/

data class CourseDetails(
    val id: String,
    val anchor: String,
    val name: String,
    val visible: Boolean,
    val stealth: Boolean,
    val sectionid: String,
    val sectionnumber: Int,
    val uservisible: Boolean,
    val hascmrestrictions: Boolean,
    val modname: String,
    val indent: Int,
    val module: String,
    val plugin: String,
    val accessvisible: Boolean,
    val url: String,
    val istrackeduser: Boolean,
    val allowstealth: Boolean
)
