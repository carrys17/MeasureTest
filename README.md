

# MeasureTest


关于在Activity中获取Measure的宽高的方法，一般直接获取是获取不到的，因为View的measure过程跟Activity的生命周期是不同步的，所以可以通过另外的方法获取。


关于View的measure过程，网上有两种错误的用法。说是违背了系统的内部实现规范（因为无法通过错误的MeasureSpec得到合法的SpecMode，从而导致Measure过程出错），其次不能保证一定能Measure出正确的结果


具体看代码。注释很清楚了。。。。
