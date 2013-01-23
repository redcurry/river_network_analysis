import java.io.*;
import java.util.*;

public class FlowData
{

    public class MouthPointIterator implements Iterator
    {

        public boolean hasNext()
        {
            if( _nextIsReady )
            {
                return true;
            }
            else
            {
                try
                {
                    _updateNext();
                }
                catch( IOException e )
                {
                    System.err.println( e );
                }

                if( _nextMouth != null )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        public Object next()
        {
            if( hasNext() )
            {
                // Invalidate next mouth (must update next mouth again)
                _nextIsReady = false;

                return _nextMouth;
            }
            else
            {
                throw new NoSuchElementException();
            }
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        private void _updateNext() throws IOException
        {
            _isBack = true;

            for( int i = 0; i < _rows; i++ )
            {
                for( int j = 0; j < _cols; j++ )
                {
                    if( _isBack )
                    {
                        i = _i;
                        j = _j;

                        _isBack = false;
                    }

                    if( _flowAcc.get( i, j ) > 0 )
                    {
                        Point p = new Point( i, j );

                        // Get the flow direction of the current cell
                        int dir = _flowDir.get( p );

                        // Get the cell's neighbor in the flow direction
                        Point q = _getNeighborInDirection( p, dir );

                        // Is the neighbor part of the ocean?
                        if( q == null || _flowAcc.get( q ) == -9999 )
                        {
                            // This cell is a river mouth
                            _nextMouth = p;

                            // Mouth is ready for a call to next()
                            _nextIsReady = true;

                            // Update grid coordinates
                            _i = i;
                            _j = j + 1;

                            return;
                        }
                    }
                }
            }

            // No more river mouths were found
            _nextMouth = null;

            // Next mouth does not exist -- not ready
            _nextIsReady = false;
        }

        private int _i = 0;
        private int _j = 0;

        private Point _nextMouth = null;

        private boolean _nextIsReady = false;

        private boolean _isBack = false;

    }

    public FlowData( String flowAccName, String flowDirName,
                     int rows, int cols, int cutoff ) throws IOException
    {
        _rows = rows;
        _cols = cols;

        _flowAcc = new FlowAccumulationGrid( flowAccName, _rows, _cols );

        _flowAcc.setCutoff( cutoff );

        _flowDir = new FlowDirectionGrid( flowDirName, _rows, _cols );
    }

    public void close() throws IOException
    {
        _flowAcc.close();
        _flowDir.close();
    }

    public Iterator getMouthPointIterator()
    {
        return new MouthPointIterator();
    }

    public Point getMainStemPoint( Point p ) throws IOException
    {
//        if( !_cellChanged )
//        {
//            return _cellMain;
//        }

        // Get the neighbors of the cell
        Point[] neighbors = getNeighbors( p );

        // No main stem point exists yet
        Point main = null;

        for( int i = 0; i < 8; i++ )
        {
            // Is the neighbor upstream?
            if( isUpstream( neighbors[i], p ) )
            {
                // If a main has not been set yet,
                // set it to any upstream neighbor
                if( main == null )
                {
                    main = neighbors[i];
                }

                // Does the current neighbor have a higher flow
                // accumulation than the main stem point?
                if( _flowAcc.get( neighbors[i] ) > _flowAcc.get( main ) )
                {
                    // Yes, then update the main stem point
                    main = neighbors[i];
                }
            }
        }

        // Update stored values to improve efficiency
        _cellChanged = true;
        _currentCell = p;
        _cellMain    = main;

        // Return the main stem point
        return main;
    }

    public Point[] getTributaryPoints( Point p ) throws IOException
    {
        // Create the list to store the cell's tributaries
        List tribs = new Vector();

        // Get the neighbors of the cell
        Point[] neighbors = getNeighbors( p );

        // Get the upstream main stem point
        Point main = getMainStemPoint( p );

        for( int i = 0; i < 8; i++ )
        {
            // Is the neighbor upstream?
            if( isUpstream( neighbors[i], p ) )
            {
                // Yes, but does the upstream main stem point exist?
                if( main != null )
                {
                    // Yes, but make sure it's not equal to the neighbor
                    if( !neighbors[i].equals( main ) )
                    {
                        // Good, neighbor is a tributary
                        tribs.add( neighbors[i] );
                    }
                }
                else
                {
                    // No, so neighbor must be a tributary
                    tribs.add( neighbors[i] );
                }
            }
        }

        // Convert the list of tributaries to an array of tributaries
        Point[] tribArray = new Point[ tribs.size() ];
        tribs.toArray( tribArray );

        // Return the array of tributaries
        return tribArray;
    }

    public Point[] getNeighbors( Point p )
    {
//        if( !_cellChanged )
//        {
//            return _cellNeighbors;
//        }

        Point[] neighbors = new Point[8];

        neighbors[0] = new Point( p.i,     p.j + 1 );
        neighbors[1] = new Point( p.i + 1, p.j + 1 );
        neighbors[2] = new Point( p.i + 1, p.j     );
        neighbors[3] = new Point( p.i + 1, p.j - 1 );
        neighbors[4] = new Point( p.i,     p.j - 1 );
        neighbors[5] = new Point( p.i - 1, p.j - 1 );
        neighbors[6] = new Point( p.i - 1, p.j     );
        neighbors[7] = new Point( p.i - 1, p.j + 1 );

        // Update stored values
        _cellChanged   = true;
        _currentCell   = p;
        _cellNeighbors = neighbors;

        return neighbors;
    }

    public boolean isUpstream( Point q, Point p ) throws IOException
    {
        // Get the flow direction of q
        int qDir = _flowDir.get( q );

        // Make sure the direction and accumulation are valid
        if( qDir > 0 && _flowAcc.get( q ) > 0 )
        {
            Point neighbor = _getNeighborInDirection( q, qDir );

            // Does the direction of flow of q point to p?
            if( neighbor != null && neighbor.equals( p ) )
            {
                // Yes, so q is upstream relative to p
                return true;
            }
            else
            {
                // No
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public int getFlowAccumulation( Point p ) throws IOException
    {
        return _flowAcc.get( p );
    }

    private Point _getNeighborInDirection( Point p, int dir )
    {
        int i = p.getI();
        int j = p.getJ();

        if( dir == FlowDirectionGrid.DIRECTION_E )
        {
            return new Point( i, j + 1 );
        }
        else if( dir == FlowDirectionGrid.DIRECTION_SE )
        {
            return new Point( i + 1, j + 1 );
        }
        else if( dir == FlowDirectionGrid.DIRECTION_S )
        {
            return new Point( i + 1, j );
        }
        else if( dir == FlowDirectionGrid.DIRECTION_SW )
        {
            return new Point( i + 1, j - 1 );
        }
        else if( dir == FlowDirectionGrid.DIRECTION_W )
        {
            return new Point( i, j - 1 );
        }
        else if( dir == FlowDirectionGrid.DIRECTION_NW )
        {
            return new Point( i - 1, j - 1 );
        }
        else if( dir == FlowDirectionGrid.DIRECTION_N )
        {
            return new Point( i - 1, j );
        }
        else if( dir == FlowDirectionGrid.DIRECTION_NE )
        {
            return new Point( i - 1, j + 1 );
        }
        else
        {
            //System.out.println( "Tell me about this." );
            return null;
        }
    }

    private FlowAccumulationGrid _flowAcc;
    private FlowDirectionGrid    _flowDir;

    private int _rows;
    private int _cols;

    private boolean _cellChanged = true;

    private Point   _currentCell;
    private Point   _cellMain;
    private Point[] _cellNeighbors;

}