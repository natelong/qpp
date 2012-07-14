package net.natelong.qpp;

import java.io.*;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.regex.*;
import java.net.URL;

public class App{

	public static Pattern includePattern = Pattern.compile( "^(?://)?(?:#include|@import) \"(.+)\"(?:;)?$" );
	public static String cacheDirectoryName = ".qpp-remote-cache";
	public static HashSet<String> includeNames = new HashSet<String>();
	public static String mainContext;

	public static void main( String[] args ) throws Throwable{
		long startTime = System.nanoTime();

		if( args.length < 2 ){
			System.out.println( "Not enough arguments" );
			System.exit( 1 );
		}
		String inputFileName = args[ args.length - 2 ];
		String outputFileName = args[ args.length - 1 ];

		includeNames.add( inputFileName );
		new File( cacheDirectoryName ).mkdir();

		BufferedWriter out = new BufferedWriter( new FileWriter( outputFileName ) );
		mainContext = contextFromFilename( inputFileName );
		processFile( getBufferedReader( inputFileName, mainContext ), out, mainContext );

		out.close();
		System.out.println( "Elapsed time: " + ( ( System.nanoTime() - startTime ) / 1000000.0f ) + " ms." );
	}

	public static void processFile( BufferedReader in, BufferedWriter out, String context ) throws Throwable{
		String tmpLine = in.readLine();
		while( tmpLine != null ){
			// check to see if the line matches the import directive
			Matcher matcher = includePattern.matcher( tmpLine );
			// if it doesn't match, just output the line, followed by a newline
			if( !matcher.matches() ){
				out.write( tmpLine );
				out.newLine();
			// if it matches
			}else{
				String fileName = matcher.group( 1 );
				// check if it has already been included
				if( !includeNames.contains( fileName )){
					// if it hasn't, process it
					String newContext = contextFromFilename( fileName );
					processFile( getBufferedReader( fileName, context ), out, context + newContext );
				}
			}
			tmpLine = in.readLine();
		}
		in.close();
	}

	public static BufferedReader getBufferedReader( String fileName, String context ) throws Throwable{
		includeNames.add( fileName );
		if( fileName.startsWith( "http://" ) || fileName.startsWith( "https://" )  ){
			String hashedFileName = MD5( fileName );
			File remoteCacheFile = new File( cacheDirectoryName + File.separator + hashedFileName );
			if( remoteCacheFile.exists() ){
				return new BufferedReader( new FileReader( remoteCacheFile ) );
			}else{
				URL input = new URL( fileName );
				BufferedReader remoteReader = new BufferedReader( new InputStreamReader( input.openStream() ) );
				BufferedWriter cacheWriter = new BufferedWriter(
						new FileWriter( cacheDirectoryName + File.separator + hashedFileName )
				);

				String tmpLine = remoteReader.readLine();
				while( tmpLine != null  ){
					cacheWriter.write( tmpLine );
					cacheWriter.newLine();
					tmpLine = remoteReader.readLine();
				}
				cacheWriter.close();
				return new BufferedReader( new FileReader( cacheDirectoryName + File.separator + hashedFileName ) );
			}
		}else{
			if( fileName.startsWith( context ) ){
				return new BufferedReader( new FileReader( fileName ) );
			}else{
				return new BufferedReader( new FileReader( context + fileName ) );
			}
		}
	}
	public static String MD5( String in ) throws Throwable{
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest( in.getBytes() );
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < array.length; ++i ){
			sb.append( Integer.toHexString( ( array[i] & 0xFF ) | 0x100 ).substring( 1, 3 ) );
		}
		return sb.toString();
	}

	public static String contextFromFilename( String fileName ){
		return fileName.substring( 0, fileName.lastIndexOf( File.separator ) + 1 );
	}
}