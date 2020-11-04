/**
 * @author - wangyu2130@sefon.com
 * @date   - 2017/7/24
 * @description - app的模块定义
 * loginModule               {Module}       登录模块
 * loginController           {Object}       登录控制器
 * loginDisabled             {Boolean}      登录按钮状态，默认禁用
 * accessSecret              {Object}       接入相关信息对象
 * warningInfo               {Object}       警告对象
 * login                     {Function}     登录
 * getAccessUser             {Function}     获取应用信息
 * checkUserExit             {Function}     检查用户是否存在
 */

(()=>{
    'use strict';

    angular
        .module('aep.login')
        .controller('loginController', loginController);

    loginController.$inject = ["$scope", "$cookies", "$state", "$translate", "appSettings", "userService", "forgetPasswordService"];

    function loginController($scope, $cookies, $state, $translate, appSettings, userService, forgetPasswordService) {
        $scope.flag = true;
        $scope.isCaption = false;
        $scope.model = {
            account: '',
            password: '',
            userType: 'individual'
        };
        $scope.Validation = true;
        $scope.ValidationGray = "";
        $scope.accessSecret = {};
        let warningInfo = {
            title: "提示信息",
            content: $translate.instant('MSG_10001')
        };
        localStorage.clear();

        let redirectMap = new Map([
            ['AEPIOT', function () {
                return $state.go('aep.access.product')
            }],
            ['IOT', function () {
                return $state.go('aep.main.device')
            }],
            ['LOCMAN', function () {
                return $state.go('aep.loc.dashboard')
            }],
            ['OPERATION', function () {
                return $state.go('aep.operation.dashboard')
            }],
            ['MAINTAIN', function () {
                return $state.go('aep.maintain.dashboard')
            }],
            ['BIGDATA', function () {
                return $state.go('aep.data.dashboard')
            }]
        ]);

        function redirectPage(v) {
            redirectMap.forEach((value, key) => {
                if (v === key) value();
            })
        }

        $scope.login = () => {
            $scope.$broadcast('validate');
            if ($scope.flag === false) {
                $scope.Validation = false;
                $scope.ValidationGray = "用户不存在，请重新输入";
                return;
            } else {
                $scope.Validation = true;
            }
            let requestParam = {
                loginAccount: $scope.model.account,
                password: (SparkMD5.hash($scope.model.password)).toUpperCase(),
                userType: $scope.model.userType
            };
            //API 校验用户密码
            userService.userLogin(requestParam).then((res) => {
                if (res.data.resultStatus.resultCode === '0000') {
                    let value = res.data.value;
                    $cookies.put(appSettings.token, value.token);
                    $cookies.put(appSettings.userId, value.userId);
                    $scope.getAccessUser();
                } else if (res.data.resultStatus.resultCode === '0005') {
                    $cookies.remove(appSettings.token);
                    $cookies.remove(appSettings.userId);
                    $scope.Validation = false;
                    $scope.ValidationGray = res.data.resultStatus.resultMessage;
                } else {
                    $cookies.remove(appSettings.token);
                    $cookies.remove(appSettings.userId);
                    $scope.Validation = false;
                    $scope.ValidationGray = "登录失败，请刷新重试";
                }
            }, () => {
            });
        };
        $scope.getAccessUser = () => {
            let userId = $cookies.get(appSettings.userId);
            if (userId !== undefined) {
                let requestParams = {
                    loginAccount: $scope.model.account,
                    password: (SparkMD5.hash($scope.model.password)).toUpperCase()
                };
                userService.getAccessUser(requestParams).then((res) => {
                    if (res.data.resultStatus.resultCode === '0000') {
                        $scope.result = res.data.value;
                        localStorage.setItem('accessInfoList', angular.toJson($scope.result));
                        
                        if ($scope.result.length === 1) {
                            $cookies.put(appSettings.accessSecret, $scope.result[0].accessSecret);
                            // redirectPage($scope.result[0].accessType);
                        }else {
                            let accessObj = {
                                accessName: '管理控制台',
                                accessType: 'MC',
                                accessSecret: ''
                            };
                            localStorage.setItem('accessObj', angular.toJson(accessObj));
                            // $state.go('aep.overview');
                            
                        }
                        $state.go('bigScreen');
                    } else {
                        $.misMsg('加载应用信息失败，请刷新重试！');
                    }
                }, () => {
                    $cookies.remove(appSettings.accessSecret);
                });
            } else {
                $state.go('login', {
                    msg: '非法登录用户，请重新登录！'
                });
            }
        };
        $scope.checkUserExit = () => {
            let emailMob = $scope.model.account;
            forgetPasswordService.judgeUser(emailMob).then(function (res) {
                let result = res.data;
                if (result.resultStatus.resultCode === '0000') {
                    if (result.value === false) {
                        $scope.Validation = false;
                        $scope.ValidationGray = "用户不存在，请重新输入";
                        $scope.flag = false;
                    } else {
                        $scope.Validation = true;
                        $scope.flag = true;
                        $scope.isCaption = true;
                    }
                }
            }, () => {
            });
        };
        $scope.$watch('$viewContentLoaded', () => {
            // angular.element('#captcha').slideToUnlock({
            //     successFunc: function () {
            //         $scope.$apply(function () {
            //             $scope.loginDisabled = !$scope.loginDisabled;
            //         });
            //     }
            // });
            //过期注销提示
            if ($state.params !== undefined && $state.params.msg !== '') $.misMsg($state.params.msg);
        });
    }
})();