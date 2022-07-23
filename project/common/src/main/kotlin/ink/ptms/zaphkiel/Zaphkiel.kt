package ink.ptms.zaphkiel

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.Zaphkiel
 *
 * @author 坏黑
 * @since 2022/7/20 01:29
 */
object Zaphkiel {

    private var api: ZapAPI? = null

    /**
     * 获取开发者接口
     */
    fun api(): ZapAPI {
        return api ?: error("ZaphkielAPI has not finished loading, or failed to load!")
    }

    /**
     * 注册开发者接口
     */
    fun register(api: ZapAPI) {
        Zaphkiel.api = api
    }
}