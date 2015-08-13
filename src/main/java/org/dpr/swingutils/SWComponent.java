package org.dpr.swingutils;

import java.awt.event.ActionListener;
import java.util.Map;

public class SWComponent
{

    public SWComponent(String label, Class<?> classe, String keyValue, Map<String, String> values, String defaultValue,
            ActionListener listener)
    {
        super();
        this.label = label;
        this.classe = classe;
        this.keyValue = keyValue;
        this.values = values;
        this.defaultValue = defaultValue;
        this.listener = listener;
    }

    private String label;
    private Class<?> classe;
    private String keyValue;
    private Map<String, String> values;
    private String defaultValue;
    private ActionListener listener;

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public Class<?> getClasse()
    {
        return classe;
    }

    public void setClasse(Class<?> classe)
    {
        this.classe = classe;
    }

    public String getKeyValue()
    {
        return keyValue;
    }

    public void setKeyValue(String keyValue)
    {
        this.keyValue = keyValue;
    }

    public Map<String, String> getValues()
    {
        return values;
    }

    public void setValues(Map<String, String> values)
    {
        this.values = values;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public ActionListener getListener()
    {
        return listener;
    }

    public void setListener(ActionListener listener)
    {
        this.listener = listener;
    }


}
