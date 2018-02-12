package hdychi.hencoderdemo.bean

data class UserBean(val code: Int = 0,
                    val loginType: Int = 0,
                    val profile: Profile?,
                    val bindings: List<BindingsItem>?,
                    val account: Account?)