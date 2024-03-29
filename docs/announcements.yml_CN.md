## announcements.yml

`announcements.yml` 中定义了所有可以被广播的公告

```yaml
###################
# 该项自 1.2.1-beta 添加
###################
Settings:
  # 限制公告被发送的默认世界
  # 空值代表一个公告会被发送到所有世界
  # 从 1.2.1-beta 开始添加这个选项
  enabled_worlds: []

announcements:
  # 公告名称，不要重复
  chat_announce_type_example:
    # 权限名，没有权限的玩家不会收到这个公告。
    # 如果什么都不写或者去除该项，则表示任何玩家都可收到这个公告
    permission: 'user.example_perm'
    # 下一个公告的延迟
    # 例子:
    # - 60s, 延迟是 60 秒
    # - 1min, 1 分钟
    delay: 60s
    # 可用的类型:
    # 1. CHAT: 该公告会被发送到聊天栏中
    # 2. TITLE: 该公告会以标题形式发送到玩家屏幕中间
    # 3. ACTION_BAR: 该公告会被发送到玩家的物品栏上方
    # 4. BOSS_BAR: 该公告会以 BOSS 栏的方式发送到玩家的屏幕上方
    # 5. MULTIPLE_LINE_BOSS_BAR: 该公告会以多行 BOSS 栏的形式发送到玩家屏幕上方. (自 1.0.2 版本起)
    type: CHAT
    # 公告内容
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    # 支持 RGB 颜色
    # 需要你的服务器版本为 1.16 及以上，并且玩家版本也需要 1.16 及以上
    #
    # RGB颜色格式: {<Color>[,<arg1>,<arg2>,...]}
    # 例子:
    # 1. {#FFFFFF}RGB颜色文本
    # 2. {#FFFFFF,&l}加粗的RGB颜色文本
    # 3. {#FFFFFF,&l,&n}加粗和下划线的RGB颜色文本
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    # 支持渐变 & 彩虹渐变
    #
    # 渐变色格式: {<Color1>-><Color2>[,<arg1>,<arg2>,...]}
    # 例子:
    # 1. {#04ff00->#bf0000}渐变文字
    # 2. {#04ff00->#bf0000,&l}加粗的渐变文字
    # 3. {#04ff00->#bf0000,&l,&n}加粗和下划线的渐变文字
    #
    # 彩虹渐变格式: {rainbow[,<arg1>,<arg2>,...]}
    # 例子:
    # 1. {rainbow}彩虹渐变文字
    # 2. {rainbow,&l}加粗彩虹渐变
    # 3. {rainbow,&l,&n}加粗和下划线的彩虹渐变文字
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    #
    # 注意:
    # 支持 PlaceholderAPI 变量
    # 如果你启用了 'Console-broadCast', 占位符会直接显示它的原始值
    # (如果这个占位符必须要以一个玩家解析的话)
    #
    # 如果你的服务器版本低于了 1.16, 那么推荐使用原版颜色代码。尽管插件对低版本做了兼容
    # (它会自动将 RGB 颜色替换成与原版最接近的颜色), 仍然不建议在低版本服务器中使用 RGB 
    # 颜色代码
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    content:
      - '{rainbow}==============================='
      - '  {#ffff00}This is an example announcement'
      - '  {#04ff00->#bf0000}gradient color text @example@ '
      - '{rainbow}==============================='
  
  action_bar_type_example:
    delay: 60s
    type: ACTION_BAR
    content:
      # 2s 的停留时间，最长是 10 秒
      # 默认停留时间为 5 秒
      - '{rainbow}this is an action bar announcement{stay:2}'
      # 在显示下一条 Action bar 时，中间会停留 2 秒
      # 默认停留时间为 5 秒
      - 'the next action bar{delay:2}'
      - 'test'

  boss_bar_type_example:
    delay: 60s
    type: BOSS_BAR
    content:
      # 可用的 BOSS 栏颜色:
      # PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
      # 可用的 BOSS 栏节段: 6, 10, 12, 20
      - '{#4d6ea5->#a7bce5}this is an boss bar announcement{stay:2}{color:GREEN}{segment:20}'
      # 需要 PAPI 拓展变量: Animations
      # 下载指令: /papi ecloud download Animations (如果你启用了 ecloud 功能)
      # 每 0.5 秒更新一次
      - '%animations_<shine start=&9 middle=&4 end=&9 normal=&b size=3>this is an auto-updated boss bar</shine>%{update:0.5}'
      # 在显示下一个 BOSS 栏时，中间会停留 2 秒
      - '&6progressing boss bar{progress:true}{delay:2}'
      # 默认停留时间是 5 秒
      # 默认颜色为 PURPLE
      - '&bthe next boss bar{segment:10}'

  title_type_example:
    delay: 60s
    type: TITLE
    # 第一行作为主标题
    # 第二行作为副标题.
    content:
      - '{#faefae->#00ff66}Example gradient title.'
      - '{rainbow}sub title'

    # 下面的配置节段只在该类型下有效
    # 淡入时间，单位：秒
    fadeIn: 0.2
    # 停留时间, 单位：秒
    stay: 3
    # 淡出时间, 单位：秒
    fadeout: 1

    # 子标题的淡入、停留、淡出时间 (从 1.2.0-beta 开始)
    sub-fadeIn: 0.5
    sub-stay: 3
    sub-fadeout: 1

  # 多行 BOSS 栏公告例子 (该类型自 1.0.2 版本增加)
  multiple_line_boss_bar_example:
    delay: 60s
    # 停留时间（秒）
    # 默认 5 秒
    stay: 3
    type: MULTIPLE_LINE_BOSS_BAR
    # 该类型中以下占位符将无法使用:
    # {stay:<double>}
    # {delay:<double>}
    content:
      # 可用的 BOSS 栏颜色:
      # PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
      # 可用的 BOSS 栏节段: 6, 10, 12, 20
      - '{#4d6ea5->#a7bce5}this is an boss bar announcement{stay:2}{color:GREEN}{segment:20}'
      # 需要 PAPI 拓展变量: Animations
      # 下载指令: /papi ecloud download Animations (如果你启用了 ecloud 功能)
      # 每 0.5 秒更新一次
      - '%animations_<shine start=&9 middle=&4 end=&9 normal=&b size=3>this is an auto-updated boss bar</shine>%{update:0.5}'
      - '&6progressing boss bar{progress:true}'
      # 默认停留时间是 5 秒
      # 默认颜色为 PURPLE
      - '&bthe next boss bar{segment:10}'


  limit_world_announcement_example:
    delay: 60s
    type: ACTION_BAR
    content:
      - '&cYou are in the nether world, be careful not to fall in to the lava!'
    
    # 限制该公告所发送的世界
    # 优先级高于默认设置
    # 如果你不配置该项内容，那么公告则会使用默认设置
    # 自从 1.2.1-beta 版本添加此选项
    worlds:
      - world_the_nether
```