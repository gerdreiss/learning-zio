import Util._

addCommandAlias("l", "projects")
addCommandAlias("ll", "projects")
addCommandAlias("ls", "projects")
addCommandAlias("cd", "project")
addCommandAlias("root", "cd zio-kafka-streaming-tutorial")
addCommandAlias("c", "compile")
addCommandAlias("ca", "Test / compile")
addCommandAlias("t", "test")
addCommandAlias("r", "run")
addCommandAlias("rs", "reStart")
addCommandAlias("s", "reStop")
addCommandAlias(
  "up2date",
  "reload plugins; dependencyUpdates; reload return; dependencyUpdates",
)

onLoadMessage +=
  s"""|
      |╭─────────────────────────────────╮
      |│     List of defined ${styled("aliases")}     │
      |├─────────────┬───────────────────┤
      |│ ${styled("l")} | ${styled("ll")} | ${styled("ls")} │ projects          │
      |│ ${styled("cd")}          │ project           │
      |│ ${styled("root")}        │ cd root           │
      |│ ${styled("c")}           │ compile           │
      |│ ${styled("ca")}          │ compile all       │
      |│ ${styled("t")}           │ test              │
      |│ ${styled("r")}           │ run               │
      |│ ${styled("rs")}          │ reStart           │
      |│ ${styled("s")}           │ reStop            │
      |│ ${styled("up2date")}     │ dependencyUpdates │
      |╰─────────────┴───────────────────╯""".stripMargin
