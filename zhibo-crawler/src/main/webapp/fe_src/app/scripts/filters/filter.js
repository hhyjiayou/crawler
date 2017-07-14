/**
 * Created by yiwei on 15/12/1.
 * @desc 定义自定义filter过滤器
 */

/**
 * @desc定义输出html过滤器
 */
commonModule.filter('to_html', function ($sce) {
    return function (text) {
        return $sce.trustAsHtml(text);
    }
});
