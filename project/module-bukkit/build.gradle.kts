taboolib { subproject = true }

dependencies {
    api(project(":project:common"))
    api(project(":project:common-impl"))
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11802:11802-minimize:mapped")
    compileOnly("ink.ptms.core:v11802:11802-minimize:universal")
}