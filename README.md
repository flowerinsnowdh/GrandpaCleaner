# GrandpaCleaner
爷清洁，一个针对 1.21.4-Folia 的掉落物清理插件

仅支持 Folia

# 功能
## 清理掉落物
每隔一段时间清理一次掉落物，可以在 `config.yml` 中配置周期和排除物品类型等

## 回收站
被清理掉的掉落物会被暂时存放进回收站，直到下次清理，可以使用 `/grandpacleaner recycle` 打开并重新获取

此举需要 `grandpacleaner.recycle` 权限

注：这是一个可以无限拿取物品的功能，请不要给普通玩家

## 重载
使用 `/grandpacleaner reload` 重新加载插件，此举会重新读取配置文件并重新计时

此举需要 `grandpacleaner.reload` 权限

## 推迟
使用 `/grandpacleaner delay` 可以推迟一次清理倒计时

此举需要 `grandpacleaner.delay` 权限

注：这是一个可以无限推迟的功能，请不要给普通玩家
