package org.eclipse.gemini.blueprint.iandt;

import java.text.MessageFormat;

public class TestUtils
{

    public static String filterString(final String property, final String value)
    {
        return MessageFormat.format("({0}={1})", property, value);
    }

    public static String andFilterString(final String... filters)
    {
        final StringBuilder filterBuilder = new StringBuilder("(&");
        for (String filter : filters)
        {
            filterBuilder.append(filter);
        }
        filterBuilder.append(")");
        return filterBuilder.toString();
    }

}
