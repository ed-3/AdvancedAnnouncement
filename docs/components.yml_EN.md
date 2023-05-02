## Components.yml

```yaml
# TextComponents that will be showed into chat box.
# ONLY USABLE ON CHAT TYPE ANNOUNCEMENT.

# component name, DO NOT HAVE DUPLICATE NAME.
example:
  # the text will be shown.
  text: '&f[&brun_command&f]'
  onClick:
    # Possible actions:
    # OPEN_URL: open a web url, its value must match like: https://www.bing.com
    # OPEN_FILE: open a file on player's local disk, its value must be an absolute path of file.
    # RUN_COMMAND: makes a player run command, its value should have the prefix '/'
    # SUGGEST_COMMAND: fill a text into player's chat box. its value can be any value.
    action: RUN_COMMAND
    value: '/say hello! this is a example action.'

  # when player's mouse cursor move to this text, what content will be shown.
  hover-value: |-
    {#f4114a->#20d820}An example hover text.
    &6you can use multiple lines.

    &f&l>>> &b&lClick here &e&lRun command
```