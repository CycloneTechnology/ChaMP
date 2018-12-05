package com.cyclone.util.shell;

/** */
public enum ShellOutputStream
{
    /** */
    STDOUT("stdout"),

    /** */
    STDERR("stderr");

    private final String streamName;

    ShellOutputStream(String a_name)
    {
        streamName = a_name;
    }

    /**
     * @return the name of the stream
     */
    public String getStreamName()
    {
        return streamName;
    }

    /**
     * The command stream for the specified name
     * 
     * @param a_name
     * @return the command stream
     */
    public static ShellOutputStream forName(String a_name)
    {
        return ShellOutputStream.valueOf(a_name.toUpperCase());
    }
}
