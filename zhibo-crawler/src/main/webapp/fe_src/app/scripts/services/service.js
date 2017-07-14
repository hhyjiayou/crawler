/**
 * Created by yiwei on 15/10/21.
 * @desc  这是一个定义自定义服务的文件
 *        定义一个CRUD等相关操作
 */
commonModule.factory('Util', function ($http, $timeout) {
  var Util = {};

  /**
   * @desc 查询函数
   */
  Util.query = function (config) {
    return $http(config);
  };

  /**
   * @desc Create
   */
  Util.insert = function (config) {
    return $http(config);
  };

  /**
   * @desc Update
   */
  Util.update = function (config) {
    return $http(config);
  };

  /**
   * @desc Delete
   */
  Util.delete = function (config) {
    return $http(config);
  };

  /**
   * @desc 拆分数组
   * @param source 需要拆分的数组
   * @param num 拆分维度
   */
  Util.array = function (source, num) {
    var result = [],
        _num = parseInt(num);
    for(var i=0,len=source.length;i<len;i+=_num){
      result.push(source.slice(i,i+_num));
    }
    return result;
  };

  /**
   * @desc 对象继承
   */
  Util.extend = function (source, target) {
    for (var o in source) {
      target[o] = source[o];
    }
    return target;
  };

  /**
   * @desc 判断是否为NaN
   */
  Util.isNaN = Number.isNaN || function (num) {
        return typeof num == 'number' && isNaN(num);
      };

  /**
   * @desc 数组去重
   */
  Util.unique = function (array) {
      var n = [];
      for (var i=0; i<array.length; i++) {
        if (n.indexOf(array[i] == -1)) n.push(array[i]);
      }
      return n;
  };

  /**
   * @desc 数组去重(对象)
   */
  Util.uniqueObj = function (array, flag) {
    var n = [].concat(array[0]);
    for (var i=0; i<array.length; i++) {
      var repeat = false;
      for (var j= 0, size=n.length; j<size; j++) {
        if(array[i][flag] == n[j][flag]){
          repeat = true;
          break;
        }
      }
      if (!repeat) {
        n.push(array[i]);
      }
    }
    return n;
  };

  /**
   * @desc 展示错误提示
   */
  Util.showAlert = function (type, msg) {
    if (!!Util.timer) {
      $timeout.cancel(Util.timer);
    }
    var that = this;
    that.alert = {
      success: type == 'success',
      process: type == 'process',
      fail: type == 'fail',
      msg: msg,
      show: true
    };
    if (type != 'process') {
      that.loading = false;
      Util.timer = $timeout(function () {
        that.alert.show = false;
      }, 3000);
    }
  };

  /**
   * @desc 判断是否为正确的url
   * @param str_url
   * @returns {boolean}
   * @constructor
   */
  Util.IsURL = function (str_url){
    var reg = /(http:\/\/|https:\/\/)((\w|=|\?|\.|\/|&|-)+)/i;
    //re.test()
    return reg.test(str_url);
  };

  return Util;
});
