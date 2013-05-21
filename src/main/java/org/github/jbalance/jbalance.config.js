/**
 * Default JBalance config file.
 * User: jjb
 * Date: 13-5-19
 * Time: 下午3:44
 */

// constant definition
var ThreadMode = {
    LITE_THREAD: "org.github.jbalance.LiteThreadPool",
    JAVA_THREAD: "org.github.jbalance.JavaThreadPool"
};

var Listener = {
    DEFAULT: "org.github.jbalance.DefaultListener"
};

var Policy = {
    DEFAULT: "org.github.jbalance.DefaultPolicy"
};

var Handler = {
    PORT_REDIRECT: "org.github.jbalance.PortRedirectHandler"
};

function getConfig(){

    return {
        ports: [
            {address:"127.0.0.1", port:81}
            ,{address:"127.0.0.1", port:8180}
        ],
        listeners: [
            {listener: Listener.DEFAULT, policies:[]}
        ],
        policies: [],
        handlers: [
            Handler.PORT_REDIRECT
            , {type: Handler.PORT_REDIRECT, properties:{a:"1", b:3}}
        ]

    };

}


