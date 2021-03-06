/**
 * date:2019/08/16
 * author:Mr.Chung
 * description:此处放layui自定义扩展
 */

window.rootPath = (function (src) {
    src = document.scripts[document.scripts.length - 1].src;
    return src.substring(0, src.lastIndexOf("/") + 1);
})();

layui.config({
    base: rootPath + "lay-module/",
    version: true
}).extend({
    layuimini: "layuimini/layuimini", // layuimini扩展
    step: 'step-lay/step', // 分步表单扩展
    treetable: 'treetable-lay/treetable', //table树形扩展（）
    tableSelect: 'tableSelect/tableSelect', // table选择扩展
    iconPickerFa: 'iconPicker/iconPickerFa', // fa图标选择扩展
    echarts: 'echarts/echarts', // echarts图表扩展
    echartsTheme: 'echarts/echartsTheme', // echarts图表主题扩展
    wangEditor: 'wangEditor/wangEditor', // wangEditor富文本扩展
    treeTable: 'treeTable/treeTable', //树形表格（此为最新treeTable）
    treeSelect: 'treeSelect/treeSelect', //下拉树控件，不支持多选
    selectM: 'select_ext/selectM', //下拉选择，支持远程加载
    xmSelect: 'xm-select/xm-select', //下拉树（单选多选），下拉列表,不支持远程加载
    notice: 'notice/notice'
});