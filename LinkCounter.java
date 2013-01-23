import java.io.*;
import java.util.*;

public class LinkCounter
{

    public static void main( String[] args ) throws Exception
    {
        _flowData = new FlowData( FLOW_ACC_PATH_NAME,
                                  FLOW_DIR_PATH_NAME,
                                  ROWS, COLS, 1 );

        for( Iterator i = _flowData.getMouthPointIterator(); i.hasNext(); )
        {
            Point p = (Point)i.next();
            //System.out.println( p );

            traverseStream( p, true );
        }
        //traverseStream( new Point( 6079, 5513 ), true );

        _flowData.close();
    }

    public static void traverseStream( Point p, boolean isMouth ) throws IOException
    {
        int tribCount = 0;

        //System.out.println( _flowData.getFlowAccumulation( p ) );

        while( true )
        {
            Point[] tribs = _flowData.getTributaryPoints( p );

            tribCount += tribs.length;

            for( int i = 0; i < tribs.length; i++ )
            {
                traverseStream( tribs[i], false );
            }

            Point main = _flowData.getMainStemPoint( p );

            p = main;

            if( p == null )
            {
                if( isMouth )
                {
                    System.out.println( tribCount );
                }
                else
                {
                    System.out.println( tribCount + 1 );
                }

                return;
            }
        }
    }

    public static String FLOW_ACC_PATH_NAME =
        "C:\\Documents and Settings\\Carlos\\Desktop\\HYDRO1k\\Flow\\na_fa.bil";

    public static String FLOW_DIR_PATH_NAME =
        "C:\\Documents and Settings\\Carlos\\Desktop\\HYDRO1k\\Flow Dir\\na_fd.bil";

    public static int ROWS = 8384;
    public static int COLS = 9102;

    public static FlowData _flowData;

}