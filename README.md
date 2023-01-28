### AdvancedAnnouncement: Automatically announcement.

[简体中文](README_zh.md)

#### Contents
+ [Announcement types](#Announcement-types)
+ [Commands](#Command)
+ [Configs](#configs)
+ [usage of command '/aa parse'](#usage-of-command-aa-parse)
+ [Image view](#image-view)
    + [CHAT type](#CHAT-type)
    + [ACTION_BAR type](#ACTION_BAR-type)
    + [BOSS_BAR type](#BOSS_BAR-type)
    + [TITLE type](#TITLE-type)

#### Announcement types
All available announcements are as below:

| announcement type | instruction |
| ------------ | --- |
| CHAT         | this announcement type will be sent to chat box |
| ACTION_BAR   | this announcement type will be sent to player's action-bar |
| BOSS_BAR     | this announcement type will be displayed by a boss bar |
| TITLE        | this announcement type will be sent through a title |
| PRE_ANNOUNCE | this announcement type will be sent to players at any time that you want |

#### Command
| Command | permission | description |
|---|---|---|
|/aa help     | -------------------- | show plugin help        |
|/aa broadcast| aa.command.broadcast | broadcast an announcement     |
|/aa display  | aa.command.display   | display an announcement to you  |
|/aa list     | aa.command.list      | list all loaded announcements |
|/aa parse    | aa.command.parse     | parse a string with placeholders    |
|/aa reload   | aa.command.reload    | reload this plugin  |

#### configs
+ #### config.yml
```yaml
# translations:
# zh_CN (简体中文) | en_US (English)
translation: zh_CN

# defines whether an announcement should be printed into console
# ONLY CHAT TYPE AND PRE_ANNOUNCE TYPE WILL BE DISPLAYED
Console-broadCast: true

# enables PlaceholderAPI support
PlaceholderAPI-support: true

# pre-announce time date format
date-format: yyyy-MM-dd HH:mm:ss

# random announcement used.
random: true

# debug test
debug: false
```
+ #### components.yml
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
+ #### announcement.yml
```yaml
announcements:
  # announce name, DO NOT HAVE DUPLICATE NAME.
  chat_announce_type_example:
    # player who don't have this permission will not receive this announcement.
    # if empty or don't write this, that refers to any player can receive this announcement.
    permission: 'user.example_perm'
    # delay to the next announcement
    # use parameter after a number to represent delay time.
    # examples:
    # - 60s, the delay time is 60 seconds
    # - 1min, the delay time is 1 minute
    # - 1h, the delay time is 1 hour (are you sure?)
    # - 1d, the delay time is 1 day (are you sure?)
    # - 1week, the delay time is 1 week(hey? are you serious?)
    # - 1month, the delay time is 1 month (hey? are you serious?)
    # - 1year, the delay time is 1 year (hey? are you serious?)
    delay: 60s
    # Possible types:
    # CHAT: this announcement type will be sent to chat box.
    # ACTION_BAR: this announcement type will be sent to player's action-bar.
    # BOSS_BAR: this announcement type will be displayed by a boss bar.
    # TITLE: this announcement type will be sent through a title.
    # PRE_ANNOUNCE: this announcement type will be sent to players at any time that you want.
    type: CHAT
    # Content of this announcement.
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    # RGB code supported
    # REQUIRES YOUR SERVER VERSION IS 1.16 OR ABOVE
    #
    # Formats:
    # RGB:
    # {#FFFFFF}RGB colored text
    # {#FFFFFF,&l}bolded RGB color text
    # {#FFFFFF,&l,&n} bolded and underlined RGB text
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    # gradient & rainbow color supported
    #
    # gradient text format: {<Color1>-><Color2>[,<arg1>,<arg2>,...]}
    # examples:
    # 1. {#04ff00->#bf0000}gradient text
    # 2. {#04ff00->#bf0000,&l}bolded gradient text
    # 3. {#04ff00->#bf0000,&l,&n} bolded and underlined gradient text
    #
    # rainbow text format: {rainbow[,<arg1>,<arg2>,...]}
    # examples:
    # 1. {rainbow}rainbow text
    # 2. {rainbow,&l}bolded rainbow text
    # 3. {rainbow,&l,&n} bolded and underlined rainbow text
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    #
    # Notice:
    # PlaceholderAPI supported.
    # If you enabled config 'Console-broadCast', placeholder will directly display ITS RAW TEXT
    # (if this placeholder must be parsed with a player.)
    #
    # If your server version is lower than 1.16, then the legacy color code is recommended.
    # Although the plugin is color compatible with a lower version (it will automatically replace
    # the RGB color with the closest legacy color), we still do not recommend using RGB color code
    # if the server version is lower than 1.16.
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    content:
      - '{rainbow}==============================='
      - '  {#ffff00}This is an example announcement'
      - '  {#04ff00->#bf0000}gradient color text @example@ '
      - '{rainbow}==============================='
  # the action bar type example
  action_bar_type_example:
    # anyone can receive this announcement
    permission: ''
    delay: 60s
    type: ACTION_BAR
    content:
      # 2s display, max time is 10
      - '{rainbow}this is an action bar announcement{stay:2}'
      # default display time: 5s
      # delay to next bar is 2s
      # default delay time is sets to 0
      - 'the next action bar{delay:2}'
      - 'test{stay:2}'

  boss_bar_type_example:
    delay: 60s
    type: BOSS_BAR
    # the same as action bar type
    content:
      # Possible bar color:
      # PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
      # Possible segment count: 6, 10, 12, 20
      - '{#4d6ea5->#a7bce5}this is an boss bar announcement{stay:2}{color:GREEN}{segment:20}'
      # requires placeholder expansion: Animations
      # command: /papi ecloud download Animations (if you enabled ecloud feature)
      - '%animations_<shine start=&9 middle=&4 end=&9 normal=&b size=3>this is an auto-updated boss bar</shine>%{update:0.5}'
      # delay to next bar is 2s
      # default delay time is sets to 0
      - '&6progressing boss bar{progress:true}{delay:2}'
      # sets segments to 10
      # default delay sets to 5 sec
      # default color sets to PURPLE
      - '&bthe next boss bar{segment:10}'

      # this type will display 2 boss_bars for player
      - - '&6&lappear with two line{stay:5}{progress:true}'
        - '&b&lanother line, time remains [sec]s{stay:5}{progress:true}{update:1}'

  title_type_example:
    delay: 60s
    type: TITLE
    # the first line is major title.
    # the second line is subtitle.
    # MUST BE TWO LINES, DO NOT WRITE THREE OR MORE LINES
    content:
      - '{#faefae->#00ff66}Example gradient title.'
      - '{rainbow}sub title'

    # CONFIGS BELOW ONLY POSSIBLE IN THIS TYPE
    # title fadeIn time, Unit: seconds
    fadeIn: 0.2
    # title stay time, Unit: seconds
    stay: 3
    # title fadeout time, Unit: seconds
    fadeout: 1

  pre_announce_example:
    # delay time to next announcement
    delay: 60s
    # defines when this announcement will send to player
    # time format can be defined in config.yml
    date: '2024-1-1 00:00:00'
    # refers to a pre-announcement
    type: PRE_ANNOUNCE
    # defines When the time arrives, what form will this announcement be displayed.
    # name is the same as above, BUT NOT EQUALS 'PRE_ANNOUNCE' type.
    displayType: CHAT
    content:
      - '{rainbow}==============================='
      - ''
      - '{#bf0000->#00bfbf,&l}HAPPY NEW YEAR!'
      - ''
      - '{rainbow}==============================='
```

#### usage of command '/aa parse'
![parse.gif](https://s2.loli.net/2023/01/23/8vhblmMaDtGoCSZ.gif)

#### Image view
+ #### CHAT type
  ![chatType.gif](https://s2.loli.net/2023/01/23/mszgIDpcWUR6TAn.gif)

+ #### ACTION_BAR type
  ![actionBarType.gif](https://s2.loli.net/2023/01/23/F2qKZnX1u8GcCx5.gif)

+ #### BOSS_BAR type
  ![bossBarType.gif](https://s2.loli.net/2023/01/23/iZnydepk68mOgzM.gif)

+ #### TITLE type
  ![titleType.gif](https://s2.loli.net/2023/01/23/gmNuj3ShGzpB5DK.gif)

+ #### PRE_ANNOUNCE type
    this type will be displayed using the 4 types above.