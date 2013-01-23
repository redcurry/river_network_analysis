public class Point
{

    public Point()
    {
    }

    public Point( int iVal, int jVal )
    {
        i = iVal;
        j = jVal;
    }

    public String toString()
    {
        return "(" + i + ", " + j + ")";
    }

    public boolean equals( Object o )
    {
        Point p = (Point)o;

        if( p != null )
        {
            return p.i == i && p.j == j;
        }
        else
        {
            return false;
        }
    }

    public int getI()
    {
        return i;
    }

    public int getJ()
    {
        return j;
    }

    public int i = 0;
    public int j = 0;

}