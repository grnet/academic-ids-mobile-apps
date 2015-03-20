//
//  Offers_CheckMainMenuViewController.m
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 27/03/2014.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import "Offers_CheckMainMenuViewController.h"
#import "Offers_CheckOffersListViewController.h"
#import "Offers_CheckAppDelegate.h"
#import "YAJLiOS/YAJL.h"

@implementation Offers_CheckMainMenuViewController
@synthesize controllers;
@synthesize validationActivityIndicator;
@synthesize justLoggedIn;


#pragma mark -
#pragma mark View lifecycle


- (void)viewDidLoad {
    // set view title
	self.title = @"Προσφορές";
	
	//create the logout button
	UIBarButtonItem *btnLogout = [[UIBarButtonItem alloc] initWithTitle:@"Έξοδος" style:UIBarButtonItemStyleBordered target:self action:@selector(logout:)];
	self.navigationController.topViewController.navigationItem.leftBarButtonItem = btnLogout;
	
	[btnLogout release];
	
	//initiate last update "label"
	UIButton *updateMsgButton = [UIButton buttonWithType:UIButtonTypeCustom];
	updateMsgButton.bounds = CGRectMake(0, 0, 235, 30);
	[updateMsgButton setTitle:@"Πιέστε το πλήκτρο ανάκτησης" forState:UIControlStateNormal];
	[updateMsgButton.titleLabel setFont:[UIFont fontWithName:@"Helvetica" size:12.0]];
	UIBarButtonItem *updateMsgButtonItem = [[UIBarButtonItem alloc] initWithCustomView:updateMsgButton];
	NSMutableArray *bottomBarItems = [toolBar.items mutableCopy];
	[bottomBarItems addObject:updateMsgButtonItem];
	[bottomBarItems exchangeObjectAtIndex:1 withObjectAtIndex:2];
	
	
	toolBar.items = bottomBarItems;
	
	[updateMsgButton release];
	[updateMsgButtonItem release];
	[bottomBarItems release];
	
	// initiate the array of controllers
	NSMutableArray *array = [[NSMutableArray alloc] init];
	
	Offers_CheckOffersListViewController *offersListController = [[Offers_CheckOffersListViewController alloc] initWithStyle:UITableViewStylePlain];
	offersListController.title = @"Ενεργές";
	[array addObject:offersListController];
	[offersListController release];
	
	offersListController = [[Offers_CheckOffersListViewController alloc] initWithStyle:UITableViewStylePlain];
	offersListController.title = @"Ανενεργές";
	[array addObject:offersListController];
	[offersListController release];
	
	self.controllers = array;
	[array release];
	
	[super viewDidLoad];
}


- (void)viewWillAppear:(BOOL)animated
{
	if ([self justLoggedIn]) {
		[self setJustLoggedIn:NO];
		[self getOffers];
	}

	[super viewWillAppear:animated];
}


#pragma mark -
#pragma mark Main Menu functionality


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
	[self.navigationController.view setUserInteractionEnabled:YES];  
	
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:@"Υπάρχει πρόβλημα στη σύνδεση δικτύου!"
												   delegate:nil cancelButtonTitle:@"ΟΚ" otherButtonTitles:nil];
	
	[alert show];		
	[alert release];
	
	NSLog(@"Connection failed: %@", [error description]);
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	[connection release];	
	
	[validationActivityIndicator stopAnimating];
	[self.navigationController.view setUserInteractionEnabled:YES];  
	
	NSString *responseString = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
	
	NSLog(@"responsestring=%@",responseString);
	
	NSDictionary *responseDictionary = [responseString yajl_JSON];
	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];

	if ([[responseDictionary objectForKey:@"response"] isEqualToString:@"SUCCESS"])
	{
		[appDelegate setOffersDict:[responseDictionary objectForKey:@"discountOffers"]];
		[(UIButton *)[[toolBar.items objectAtIndex:1] view] setTitle:[@"Ενημερώθηκε την " stringByAppendingString:[self getCurrentTime]] forState:UIControlStateNormal];
	}
	else {
		[self logoutProcess];
	}
		
	[responseData release];
	[responseString release];
}


- (IBAction)logout:(id)sender 
{
	[self logoutProcess];
}


-(void)logoutProcess
{
	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	
	[appDelegate setAuthValue:nil];
	[appDelegate setUserName:nil];
	[appDelegate setPwdMD5Stored:nil];
	[appDelegate switchToView1:self.navigationController.view];
}


-(NSString *)getCurrentTime
{
	NSDate *now = [NSDate date];
	NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
	[dateFormatter setDateFormat:@"dd/MM/yyyy hh:mma"];
	NSString *currentTime = [dateFormatter stringFromDate:now];
	[dateFormatter release];
	
	return currentTime;
}


-(IBAction)refresh:(id)sender
{
	[self getOffers];
}


-(IBAction)viewSettings:(id)sender
{
	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	[appDelegate switchToView3:self.navigationController.view];
}


-(void)getOffers
{
	responseData = [[NSMutableData data] retain];
	
	NSString *urlString = [[NSString alloc] initWithFormat:@"%@loadProviderOffers", appBaseURL];
	
	NSLog(@"urlString=%@",urlString);
	
	[self.navigationController.view setUserInteractionEnabled:NO];  
	[validationActivityIndicator startAnimating];
	
	NSMutableURLRequest *mutableRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestReloadIgnoringCacheData
															  timeoutInterval:12];
	
	[urlString release];
	
	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];	
	
	[mutableRequest setHTTPMethod:@"GET"];
	[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"CONTENT-TYPE"];
	[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"ACCEPTS"];
	[mutableRequest setValue:[appDelegate authValue] forHTTPHeaderField:@"AUTHORIZATION"];
	
	NSLog(@"HTTP request headers: %@", [mutableRequest allHTTPHeaderFields]);
	
	[[NSURLConnection alloc] initWithRequest:mutableRequest delegate:self];			
}


#pragma mark -
#pragma mark Table view data source


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return [self.controllers count];
}


// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"MainMenuCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    
    // Configure the cell...
    NSUInteger row = [indexPath row];
	Offers_CheckOffersListViewController *controller = [controllers objectAtIndex:row];
	cell.textLabel.text = controller.title;
	cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
	
    return cell;
}


#pragma mark -
#pragma mark Table view delegate


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	NSUInteger row = [indexPath row];
	Offers_CheckOffersListViewController *nextController = [self.controllers objectAtIndex:row];
	[self.navigationController pushViewController:nextController animated:YES];
}


#pragma mark -
#pragma mark Memory management


- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
}

- (void)viewDidUnload {
	self.controllers = nil;
	[super viewDidUnload];
}


- (void)dealloc {
	[controllers release];
	[validationActivityIndicator release];
    [super dealloc];
}


@end

