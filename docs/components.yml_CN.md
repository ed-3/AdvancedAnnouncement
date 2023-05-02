## Components.yml
聊天文本组件配置文件

```yaml
# 将会被显示在聊天栏中的 JSON 文本组件
# 只在 CHAT 类型公告下有用。
# 通过 @<example_name>@ 调用

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
```