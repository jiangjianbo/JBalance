package org.github.jbalance;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: jjb
 * DateTime: 2013-05-01 18:47
 */
public class JBalanceMain {

    public static final String JBALANCE_HELP_MESSAGE = "org.github.jbalance.jbalance-message";
    public static ResourceBundle helpMessage = ResourceBundle.getBundle(JBALANCE_HELP_MESSAGE, Locale.getDefault());
    public static final String JBALANCE_USAGE = "jbalance.usage";

    public static int main(String[] args){
        InetAddress[] listenPorts = null;
        Class<Listener> listenerClass = null;
        Class<Handler>[] handlers = null;
        boolean threadMode = true; // true= thread, false= lite

        boolean threadUse = false;
        ArrayList<InetAddress> ports = new ArrayList<InetAddress>();
        ArrayList<Class<Handler>> hc = new ArrayList<Class<Handler>>();

        int index = 0;
        while( index < args.length){
            if( "-h".compareToIgnoreCase(args[index]) == 0 ){
                System.out.println(helpMessage.getString(JBALANCE_USAGE));
                return 0;
            }
            else if( "-port".compareToIgnoreCase(args[index]) == 0 ){
                if( index+1 >= args.length )
                    return errorExit("\'-port\' need ports! example: *:80,127.0.0.1:8080,25");

                ++ index;
                try {
                    String item = args[index];
                    if(item.length() > 0)
                        ports.add(Utils.parseInetAddress(item));
                } catch (NumberFormatException e) {
                    return errorExit("port number must be integer");
                }
            }
            else if( "-listener".compareToIgnoreCase(args[index]) == 0 ){
                if( index+1 >= args.length )
                    return errorExit("\'-listener\' need classname!");

                ++ index;
                try {
                    listenerClass = (Class<Listener>) Class.forName(args[index]);
                } catch (ClassNotFoundException e) {
                    return errorExit("Class not found: " + args[index]);
                }
            }
            else if( "-thread".compareToIgnoreCase(args[index]) == 0 ){
                if( threadUse ) {
                    return errorExit("-thread or -lite can only use one time");
                }
                threadUse = true;
                threadMode = true;
            }
            else if( "-lite".compareToIgnoreCase(args[index]) == 0 ){
                if( threadUse ) {
                    return errorExit("-thread or -lite can only use one time");
                }
                threadUse = true;
                threadMode = false;
            }
            else if( "-handler".compareToIgnoreCase(args[index]) == 0 ){
                if( index+1 >= args.length )
                    return errorExit("\'-handler\' need classname list! example: a.b.classA,c.d.classB");

                ++ index;
                try {
                    String item = args[index];
                    if(item.length() > 0)
                        hc.add((Class<Handler>) Class.forName(item));
                } catch (ClassNotFoundException e) {
                    return errorExit("port number must be integer");
                }
            }
            else if( "".compareToIgnoreCase(args[index]) == 0 ){}
            else if( "".compareToIgnoreCase(args[index]) == 0 ){}
            ++index;
        }

        if( ports.size() == 0 )
            return errorExit("ports required");

        listenPorts = new InetAddress[ports.size()];
        for(int j = 0; j < ports.size(); ++j)
            listenPorts[j] = ports.get(j);

        if( hc.size() > 0 )
            handlers = (Class<Handler>[]) hc.toArray(new Class<?>[0]);

        try {
            new JBalanceRunner(Utils.newInstance(listenerClass == null? DefaultListener.class: listenerClass)
                    , listenPorts
                    , Utils.newInstance(handlers == null ? (Class<Handler>[])new Class<?>[]{DefaultHandler.class} : handlers)
                    , threadMode).run();
        } catch (IllegalAccessException e) {

        } catch (InstantiationException e) {

        }

        return 0;
    }

    private static int errorExit(String errorMessage) {
        System.out.println(errorMessage);
        return -1;
    }
}
