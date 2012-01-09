package net.natelong.qpp;

import java.io.*;
import java.security.MessageDigest;
import java.util.regex.*;
import java.net.URL;

public class App{

	public static Pattern includePattern = Pattern.compile( "^#include \"(.+)\"$" );
	public static String cacheDirectoryName = ".qpp-remote-cache";
	
	public static void main( String[] args ) throws Throwable{
		long startTime = System.nanoTime();

		if( args.length < 2 ){
			System.out.println( "Not enough arguments" );
			System.exit( 1 );
		}
		String inputFileName = args[ args.length - 2 ];
		String outputFileName = args[ args.length - 1 ];

		new File( cacheDirectoryName ).mkdir();

		BufferedWriter out = new BufferedWriter( new FileWriter( outputFileName ) );
		String context = inputFileName.substring( 0, inputFileName.lastIndexOf( File.separator ) + 1 );
		processFile( getBufferedReader( inputFileName, "" ), out, context );

		out.close();
		System.out.println( "Elapsed time: " + ( ( System.nanoTime() - startTime ) / 1000000.0f ) + " ms." );
	}

	public static void processFile( BufferedReader in, BufferedWriter out, String context ) throws Throwable{
		String tmpLine = in.readLine();
		while( tmpLine != null ){
			Matcher matcher = includePattern.matcher( tmpLine );
			if( !matcher.matches() ){
				out.write( tmpLine );
				out.newLine();
			}else{
				processFile( getBufferedReader( matcher.group( 1 ), context), out, context );
			}
			tmpLine = in.readLine();
		}
		in.close();
	}

	public static BufferedReader getBufferedReader( String fileName, String context ) throws Throwable{
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
			return new BufferedReader( new FileReader( context + fileName ) );
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
}