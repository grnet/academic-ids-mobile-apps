//
//  AcademicID_CheckView3Controller.m
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 3/27/14.
//  Copyright 2014 __GRNET__. All rights reserved.
//


#import "Offers_CheckView3Controller.h"
#import "Offers_CheckAppDelegate.h"
#import "Offers_CheckViewController.h"
#import "YAJLiOS/YAJL.h"
#import "NSData+Base64.h"
#import <CommonCrypto/CommonDigest.h>


@implementation Offers_CheckView3Controller
@synthesize existingPWD;
@synthesize theNewPWD;
@synthesize theNewPWDVerification;
@synthesize validationActivityIndicator;
@synthesize scrollView;


#pragma mark -
#pragma mark Change pwd functionality


-(void)clearTextFields
{
	existingPWD.text = nil;
	theNewPWD.text = nil;
	theNewPWDVerification.text = nil;
}


-(void)showMessage:(NSString *)msg
{
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:msg
												   delegate:nil cancelButtonTitle:@"Κλείσιμο" otherButtonTitles:nil];
	
	[alert show];		
	[alert release];
}


-(IBAction)stepBack:(id)sender
{
	[self clearTextFields];
	
	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	[appDelegate switchToMainMenuView:self.view];
}


-(IBAction)savePWD:(id)sender
{
	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	
	if (([[existingPWD text] length] == 0) || ([[theNewPWD text] length] == 0) || ([[theNewPWDVerification text] length] == 0))
	{
		[self showMessage:@"Όλα τα πεδία είναι υποχρεωτικά!"];
	}
	else if (![[Offers_CheckViewController md5:[[NSString alloc] initWithFormat:@"%@", [existingPWD text]]] isEqualToString:[appDelegate pwdMD5Stored]])
	{
		[self showMessage:@"Ο τρέχων κωδικός πρόσβασης δεν είναι σωστός. Παρακαλώ δοκιμάστε ξανά"];
	}
	else if ([[theNewPWD text] length] < 5)
	{
		[self showMessage:@"Ο νέος κωδικός πρόσβασης πρέπει να έχει τουλάχιστον 5 χαρακτήρες"];
	}
	else if ([[theNewPWD text] length] > 20)
	{
		[self showMessage:@"Ο νέος κωδικός πρόσβασης δε μπορεί να υπερβαίνει τους 20 χαρακτήρες"];
	}
	else if (![[theNewPWD text] isEqualToString:[theNewPWDVerification text]])
	{
		[self showMessage:@"Η επαλήθευση του νέου κωδικού πρόσβασης δεν είναι σωστή!"];
	}
	else if ([[existingPWD text] isEqualToString:[theNewPWD text]])
	{
		[self showMessage:@"Ο τρέχων και ο νέος κωδικός πρόσβασης δε μπορεί να είναι ίδιοι"];
	}
	else 
	{
		responseData = [[NSMutableData data] retain];
		NSString *urlString = [[NSString alloc] initWithFormat:@"%@updatePassword", appBaseURL];
		
		NSLog(@"urlString=%@",urlString);
		
		[self.view setUserInteractionEnabled:NO];  
		[validationActivityIndicator startAnimating];
		
		NSMutableURLRequest *mutableRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestReloadIgnoringCacheData
																  timeoutInterval:12];
		
		[urlString release];
		
		Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
		
		[mutableRequest setHTTPMethod:@"POST"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"CONTENT-TYPE"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"ACCEPTS"];
		[mutableRequest setValue:[appDelegate authValue] forHTTPHeaderField:@"AUTHORIZATION"];
		
		NSLog(@"HTTP request headers: %@", [mutableRequest allHTTPHeaderFields]);
		
		NSString *newPwdMD5String = [Offers_CheckViewController md5:[[NSString alloc] initWithFormat:@"%@", [theNewPWD text]]];
		
		NSDictionary *bodyDictionary = [NSDictionary dictionaryWithObject:newPwdMD5String forKey:@"newPassword"]; 
		NSString *bodyJSONString = [bodyDictionary yajl_JSONString];
		
		NSMutableData *body = [NSMutableData data];
		[body appendData:[bodyJSONString dataUsingEncoding:NSUTF8StringEncoding]];
		[mutableRequest setHTTPBody:body];
		
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
	[validationActivityIndicator stopAnimating];
	[self.view setUserInteractionEnabled:YES];  
	
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:@"Υπάρχει πρόβλημα στη σύνδεση δικτύου!"
												   delegate:nil cancelButtonTitle:@"ΟΚ" otherButtonTitles:nil];
	
	[alert show];		
	[alert release];
	
	NSLog(@"Connection failed: %@", [error description]);
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	[connection release];	
	
	[validationActivityIndicator stopAnimating];
	[self.view setUserInteractionEnabled:YES];  
	NSString *responseString = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
	
	NSLog(@"responsestring=%@",responseString);
	
	NSDictionary *responseDictionary = [responseString yajl_JSON];
	if ([[responseDictionary objectForKey:@"response"] isEqualToString:@"SUCCESS"])
	{
		Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
		
		[appDelegate setPwdMD5Stored:[Offers_CheckViewController md5:[[NSString alloc] initWithFormat:@"%@", [theNewPWD text]]]];
		NSString *newPwdMD5String = [appDelegate pwdMD5Stored];
		
		NSString *authStr = [NSString stringWithFormat:@"%@:%@", [appDelegate userName], newPwdMD5String];
		NSData *authData = [authStr dataUsingEncoding:NSASCIIStringEncoding];
		[appDelegate setAuthValue:[NSString stringWithFormat:@"Basic %@", [authData base64EncodedString]]];
		
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Η αλλαγή του κωδικού σας ολοκληρώθηκε!"
													   delegate:nil cancelButtonTitle:@"ΟΚ" otherButtonTitles:nil];
		
		[alert show];		
		[alert release];
		
		[self clearTextFields];
		[appDelegate switchToMainMenuView:self.view];
		
	}
	else {
		[self showMessage:[responseDictionary objectForKey:@"errorReason"]];
	}
	
	
	[responseData release];
	[responseString release];
}


-(IBAction)textFieldDoneEditing:(id)sender 
{
	[sender resignFirstResponder];
	[scrollView setContentOffset:CGPointZero animated:YES];
}


- (void)textFieldDidBeginEditing:(UITextField *)textField {
    if (textField != existingPWD)
	{
		CGPoint scrollPoint = CGPointMake(0, textField.frame.origin.y - 34.0);
		[scrollView setContentOffset:scrollPoint animated:YES];
	}
}


#pragma mark -
#pragma mark Memory management


- (void)viewDidLoad {
	[super viewDidLoad];
	[validationActivityIndicator stopAnimating];
}


- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
}


- (void)viewDidUnload {
    [super viewDidUnload];
}


- (void)dealloc {
	[existingPWD release];
	[theNewPWD release];
	[theNewPWDVerification release];
    [super dealloc];
}


@end
