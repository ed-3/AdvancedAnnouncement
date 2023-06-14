## Components.yml
聊天文本组件配置文件

```yaml
# 通过 @<example_name>@ 调用

# 组件现在可以用在以下类型:
# CHAT, TITLE and ACTION_BAR (其中的 TITLE、ACTION_BAR 从 1.2.0-beta 开始支持)

# 名称，不要重复
example:
  # 将会被显示的文本
  text: '&f[&brun_command&f]'
  onClick:
    # 可用的选项:
    # OPEN_URL: 打开一个网页链接
    # OPEN_FILE: 打开玩家本地的一个文件，需要用绝对路径
    # RUN_COMMAND: 让玩家执行一个命令
    # SUGGEST_COMMAND: 填充一段文本到玩家的输入栏中
    action: RUN_COMMAND
    value: '/say hello! this is a example action.'

  # 当玩家鼠标停留在这段文字上，将会显示的内容
  hover-value: |-
    {#f4114a->#20d820}An example hover text.
    &6you can use multiple lines.

    &f&l>>> &b&lClick here &e&lRun command
    
# 使用 JSON 组件时，插件将不会对颜色代码进行转义。
json_example:
  type: JSON
  # 生成工具: https://minecraft.tools/en/json_text.php
  # 格式化: https://ctool.dev/tool.html#/tool/json
  # 需要 服务器/客户端 版本 1.16+
  # 格式必须为 JSON 数组形式，即用中括号: [] 包裹起来的格式，例如：
  # [{"text": "这是一条示例消息"}]
  content: |-
    [
      {
        "text": "❤Hi this is a test message❤ ",
        "color": "ff008c"
      },
      {
        "text": "["
      },
      {
        "text": "Hover Text here",
        "underlined": true,
        "color": "#10DD18",
        "hoverEvent": {
          "action": "show_text",
          "contents": [
            "",
            {
              "text": "Hi this is a",
              "color": "gold"
            },
            {
              "text": " Json Based",
              "bold": "true",
              "color": "yellow"
            },
            {
              "text": " message!\nPress ",
              "color": "gold"
            },
            {
              "text": "Shift",
              "bold": "true",
              "color": "yellow"
            },
            {
              "text": " + ",
              "color": "gold"
            },
            {
              "text": "Left Click",
              "bold": "true",
              "color": "yellow"
            },
            {
              "text": " here, you will see an text filled in your chat box!",
              "color": "gold"
            }
          ]
        },
        "insertion": "suggest text"
      },
      {
        "text": "]"
      }
    ]
```