package org.github.jbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * User: jjb
 * DateTime: 2013-05-01 18:47
 */
public class JBalanceMain {

    public static final String JBALANCE_HELP_MESSAGE = "org.github.jbalance.jbalance-message";
    public static final String JBALANCE_DEFAULT_CONFIG = "org.github.jbalance.jbalance";
    public static final String HELP_USAGE = "jbalance.help.usage";

    public static ResourceBundle helpMessage = ResourceBundle.getBundle(JBALANCE_HELP_MESSAGE, Locale.getDefault());
    public static final String CONFIG_KEY_LISTENER = "jbalance.listener";
    public static final String CONFIG_KEY_PORTS = "jbalance.ports";
    public static final String CONFIG_KEY_PORT_PREFIX = CONFIG_KEY_PORTS + ".";
    public static final String CONFIG_KEY_HANDLERS = "jbalance.handlers";
    public static final String CONFIG_KEY_HANDLER_PREFIX = CONFIG_KEY_HANDLERS + ".";
    private static final String CONFIG_KEY_THREAD = "jbalance.thread";
    private static final String CONFIG_KEY_FILE = "_config_file_";

    private static final Logger logger = LoggerFactory.getLogger(JBalanceMain.class);

    public static void main(String[] args){
        _main(args);
    }

    private static int _main(String[] args){
        // temp
        ArrayList<InetSocketAddress> ports = new ArrayList<InetSocketAddress>();
        ArrayList<Class<Handler>> hc = new ArrayList<Class<Handler>>();
        // for properties
        Map<String,Object> props = new HashMap<String, Object>();
        // load default properties
        ResourceBundle defProps = ResourceBundle.getBundle(JBALANCE_DEFAULT_CONFIG);
        if( defProps != null ){
            Map<String,String> defcopy = new HashMap<String, String>();
            for(String key : defProps.keySet())
                defcopy.put(key, defProps.getString(key));
            loadConfig(props, defcopy);
        }
        // load command line
        Map<String,String> cmdline = new HashMap<String,String>();
        int ret = loadCommandLine(args, cmdline);
        if( ret != 0 )
            return ret;

        // check if config -file exist, then load config file
        if( cmdline.containsKey(CONFIG_KEY_FILE) ){
            Properties ppp = new Properties();
            try {
                ppp.load(new FileInputStream(cmdline.get(CONFIG_KEY_FILE)));
            } catch (IOException e) {
                return errorExit("config file read error");
            }
            loadConfig(props, (Map) ppp);
        }

        // load command line args
        loadConfig(props, cmdline);

        // parse config
        // arguments
        InetSocketAddress[] listenPorts = null;
        Listener listener = null;
        Handler[] handlers = null;
        boolean threadMode = true; // true= thread, false= lite

        if( props.containsKey(CONFIG_KEY_PORTS) ){
            ArrayList<String> list = (ArrayList<String>) props.get(CONFIG_KEY_PORTS);
            listenPorts = new InetSocketAddress[list.size()];
            for(int i = 0; i < list.size(); ++i)
                listenPorts[i] = Utils.parseInetAddress(list.get(i));
        }else
            return errorExit("require listen ports");

        if( props.containsKey(CONFIG_KEY_HANDLERS) ){
            ArrayList<String> list = (ArrayList<String>) props.get(CONFIG_KEY_HANDLERS);
            handlers = new Handler[list.size()];
            for(int i = 0; i < list.size(); ++i)
                try{
                    handlers[i] = Utils.newInstance(list.get(i));
                } catch (ClassNotFoundException e) {
                    return errorExit("class not found: " + list.get(i));
                } catch (InstantiationException e) {
                    return errorExit("can not create class " + list.get(i));
                } catch (IllegalAccessException e) {
                    return errorExit("error access class " + list.get(i));
                }
        }

        if( props.containsKey(CONFIG_KEY_LISTENER) ){
            String clazz = (String) props.get(CONFIG_KEY_LISTENER);
            try {
                listener = Utils.newInstance(clazz);
            } catch (ClassNotFoundException e) {
                return errorExit("class not found: " + clazz);
            } catch (InstantiationException e) {
                return errorExit("can not create class " + clazz);
            } catch (IllegalAccessException e) {
                return errorExit("error access class " + clazz);
            }
        }

        if( props.containsKey(CONFIG_KEY_THREAD) ){
            threadMode = "thread".equalsIgnoreCase((String) props.get(CONFIG_KEY_THREAD));
        }

        DefaultDispatcher dispatcher = new DefaultDispatcher();
        dispatcher.setHandlers(handlers);
        dispatcher.setPool(threadMode? new JavaThreadPool(): new LiteThreadPool());

        new JBalanceRunner(listener,listenPorts, dispatcher).run();

        return 0;
    }

    private static int loadCommandLine(String[] args, Map<String,String> cmdline) {
        boolean threadUse = false;
        int portIndex = 0, handlerIndex = 0;

        int index = 0;
        while( index < args.length){
            if( "-h".compareToIgnoreCase(args[index]) == 0 ){
                System.out.println(helpMessage.getString(HELP_USAGE));
                return 0;
            }
            else if( "-port".compareToIgnoreCase(args[index]) == 0 ){
                if( index+1 >= args.length )
                    return errorExit("\'-port\' need ports! example: *:80,127.0.0.1:8080,25");

                cmdline.put(CONFIG_KEY_PORT_PREFIX + portIndex++, args[++index]);
            }
            else if( "-listener".compareToIgnoreCase(args[index]) == 0 ){
                if( index+1 >= args.length )
                    return errorExit("\'-listener\' need classname!");

                cmdline.put(CONFIG_KEY_LISTENER, args[++index]);
            }
            else if( "-thread".compareToIgnoreCase(args[index]) == 0 ){
                if( threadUse ) {
                    return errorExit("-thread or -lite can only use one time");
                }
                threadUse = true;
                cmdline.put(CONFIG_KEY_THREAD, "thread");
            }
            else if( "-lite".compareToIgnoreCase(args[index]) == 0 ){
                if( threadUse ) {
                    return errorExit("-thread or -lite can only use one time");
                }
                threadUse = true;
                cmdline.put(CONFIG_KEY_THREAD, "lite");
            }
            else if( "-handler".compareToIgnoreCase(args[index]) == 0 ){
                if( index+1 >= args.length )
                    return errorExit("\'-handler\' need classname list! example: a.b.classA,c.d.classB");

                cmdline.put(CONFIG_KEY_HANDLER_PREFIX + handlerIndex++, args[++index]);
            }
            else if( "-file".compareToIgnoreCase(args[index]) == 0 ){
                if( index+1 >= args.length )
                    return errorExit("\'-file\' need config file path!");

                cmdline.put(CONFIG_KEY_FILE, args[++index]);
            }
            else if( "".compareToIgnoreCase(args[index]) == 0 ){}
            ++index;
        }
        return 0;
    }

    private static void loadConfig(Map<String, Object> props, Map<String, String> defProps) {
        for(String key : defProps.keySet()) {
            String value = defProps.get(key);
            if( key.startsWith(CONFIG_KEY_PORT_PREFIX)){
                if( !props.containsKey(CONFIG_KEY_PORTS))
                    props.put(CONFIG_KEY_PORTS, new ArrayList<String>());
                ((ArrayList<String>)props.get(CONFIG_KEY_PORTS)).add(value);
            } else if( key.startsWith(CONFIG_KEY_HANDLER_PREFIX)){
                if( !props.containsKey(CONFIG_KEY_HANDLERS))
                    props.put(CONFIG_KEY_HANDLERS, new ArrayList<String>());
                ((ArrayList<String>)props.get(CONFIG_KEY_HANDLERS)).add(value);
            } else
                props.put(key, value);
        }
    }

    private static int errorExit(String errorMessage) {
        System.out.println(errorMessage);
        System.exit(-1);
        return -1;
    }
}
