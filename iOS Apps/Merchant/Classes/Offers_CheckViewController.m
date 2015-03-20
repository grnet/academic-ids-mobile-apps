//
//  Offers_CheckViewController.m
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 3/27/14.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import "Offers_CheckViewController.h"
#import "Offers_CheckAppDelegate.h"
#import "YAJLiOS/YAJL.h"
#import "NSData+Base64.h"
#import <CommonCrypto/CommonDigest.h>

@implementation Offers_CheckViewController
@synthesize usernameField;
@synthesize passwordField;
@synthesize loginActivityIndicator;
@synthesize loadUserDictionary;


#pragma mark -
#pragma mark Login procedure


-(IBAction)login:(id)sender
{
	if ([[usernameField text] length] == 0) {
		UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:@"Δεν έχετε εισάγει Όνομα Χρήστη" delegate:nil 
												  cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[alertView show];
		[alertView release];
	}
	else 
	{
		responseData = [[NSMutableData data] retain];
		
		Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
		[appDelegate setUserName:[usernameField text]];
		[appDelegate setPwdMD5Stored:[Offers_CheckViewController md5:[[NSString alloc] initWithFormat:@"%@", [passwordField text]]]];
				
		NSString *urlString = [[NSString alloc] initWithFormat:@"%@loadUser?username=%@&password=%@", appBaseURL, [usernameField text], [appDelegate pwdMD5Stored]];

		NSLog(@"urlString=%@",urlString);
		
		[self.view setUserInteractionEnabled:NO];  
		[loginActivityIndicator startAnimating];
		
		NSMutableURLRequest *mutableRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestReloadIgnoringCacheData
																  timeoutInterval:12];
		
		[urlString release];
		
		
		NSString *authStr = [NSString stringWithFormat:@"%@:%@", [usernameField text], [appDelegate pwdMD5Stored]];
		NSData *authData = [authStr dataUsingEncoding:NSASCIIStringEncoding];
		
		[appDelegate setAuthValue:[NSString stringWithFormat:@"Basic %@", [authData base64EncodedString]]];		
		
		[mutableRequest setHTTPMethod:@"GET"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"CONTENT-TYPE"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"ACCEPTS"];
		[mutableRequest setValue:[appDelegate authValue] forHTTPHeaderField:@"AUTHORIZATION"];
		
		NSLog(@"HTTP request headers: %@", [mutableRequest allHTTPHeaderFields]);
		
		[[NSURLConnection alloc] initWithRequest:mutableRequest delegate:self];		
	}
}


- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[responseData setLength:0];
}


- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[responseData appendData:data];
}


- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	[connection release];
	[responseData release];
	[loginActivityIndicator stopAnimating];
	[self.view setUserInteractionEnabled:YES];  
	
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:@"Υπάρχει πρόβλημα στη σύνδεση δικτύου!"
												   delegate:nil cancelButtonTitle:@"ΟΚ" otherButtonTitles:nil];
	
	[alert show];		
	[alert release];
	
	NSLog(@"Connection failed: %@", [error description]);
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	[connection release];	
	
	[loginActivityIndicator stopAnimating];
	[self.view setUserInteractionEnabled:YES];  
	NSString *responseString = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
	
	NSLog(@"responsestring=%@",responseString);
	
	self.loadUserDictionary = [responseString yajl_JSON];
		
	if ([[self.loadUserDictionary objectForKey:@"response"] isEqualToString:@"SUCCESS"])
	{
		Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
		[appDelegate switchToMainMenuView:self.view];
		usernameField.text = nil;
		passwordField.text = nil;
	}
	else {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:[self.loadUserDictionary objectForKey:@"errorReason"]
													   delegate:nil cancelButtonTitle:@"ΟΚ" otherButtonTitles:nil];
		
		[alert show];		
		[alert release];
		
		NSLog(@"Login failed: %@", [self.loadUserDictionary objectForKey:@"errorReason"]);
	}
	
	[responseData release];
	[responseString release];
}


-(IBAction)textFieldDoneEditing:(id)sender 
{
	[sender resignFirstResponder];
}


+ (NSString *) md5:(NSString *) input
{
    const char *cStr = [input UTF8String];
    unsigned char digest[CC_MD5_DIGEST_LENGTH];
    CC_MD5( cStr, strlen(cStr), digest ); // This is the md5 call
	
    NSMutableString *output = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
	
    for(int i = 0; i < CC_MD5_DIGEST_LENGTH; i++)
		[output appendFormat:@"%02x", digest[i]];
	
    return  output;
}


#pragma mark -
#pragma mark View lifecycle


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
	[loginActivityIndicator stopAnimating];
	
	self.loadUserDictionary = nil;	
}


- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
}


- (void)viewDidUnload {
	self.usernameField = nil;
	self.passwordField = nil;
	self.loginActivityIndicator = nil;
	self.loadUserDictionary = nil;
}


- (void)dealloc {
	[usernameField release];
	[passwordField release];
	[loginActivityIndicator release];
	[loadUserDictionary release];
    [super dealloc];
}


@end
