//
//  Offers_CheckOffersListViewController.m
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 3/27/14.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import "Offers_CheckOffersListViewController.h"
#import "Offers_CheckAppDelegate.h"
#import "Offers_CheckOfferDetailsViewController.h"


@implementation Offers_CheckOffersListViewController
@synthesize list;


#pragma mark -
#pragma mark View lifecycle


- (void)viewDidLoad {
	[self.tableView setAllowsSelection:NO]; 

    [super viewDidLoad];
}


- (void)viewWillAppear:(BOOL)animated
{
	NSMutableArray *array = [[NSMutableArray alloc] init];

	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];

	for (NSDictionary *discountOffer in appDelegate.offersDict) {
		if (([self.title isEqualToString:@"Ενεργές"]) && ([[discountOffer objectForKey:@"offerStatus"] intValue] == 3)) {
			[array addObject:discountOffer];
		}
		if (([self.title isEqualToString:@"Ανενεργές"]) && ([[discountOffer objectForKey:@"offerStatus"] intValue] != 3)) {
			[array addObject:discountOffer];
		}
	}

	self.list = array;

	[self.tableView reloadData];

	[array release];	

	[super viewWillAppear:animated];
}


#pragma mark -
#pragma mark Table view data source


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return [list count];
}


// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    
	NSUInteger row = [indexPath row];
	NSString *rowString = [((NSDictionary *)[list objectAtIndex:row]) objectForKey:@"title"];
	cell.textLabel.text = rowString;
	cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
    
    return cell;
}


#pragma mark -
#pragma mark Table view delegate


- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
	if (childController == nil) {
		childController = [[Offers_CheckOfferDetailsViewController alloc] initWithNibName:@"Offers_CheckOfferDetailsViewController" bundle:nil];
	}
		
	NSUInteger row = [indexPath row];
	
	childController.title = [((NSDictionary *)[list objectAtIndex:row]) objectForKey:@"title"];
	childController.startDate = [self getDateStrFromJSONString:[((NSDictionary *)[list objectAtIndex:row]) objectForKey:@"startDate"]];
	childController.endDate = [self getDateStrFromJSONString:[((NSDictionary *)[list objectAtIndex:row]) objectForKey:@"endDate"]];
	childController.description = [((NSDictionary *)[list objectAtIndex:row]) objectForKey:@"description"];
	NSString *criteria = [[NSString alloc] initWithFormat:@"%@",[((NSDictionary *)[list objectAtIndex:row]) objectForKey:@"criteria"]];
	childController.criteria = [criteria stringByReplacingOccurrencesOfString:@"<br>" withString:@"\n"];
	
	NSMutableDictionary *offerDetailsDict =[[NSMutableDictionary alloc] initWithDictionary:((NSMutableDictionary *)[list objectAtIndex:row]) copyItems:YES];
	[offerDetailsDict removeObjectForKey:@"criteria"];
	childController.offerDetailsDict = offerDetailsDict;
		
	[self.navigationController pushViewController:childController animated:YES];
	
	[criteria release];
	[offerDetailsDict release];
}


- (NSString *) getDateStrFromJSONString:(NSString*)epochTime
{
	NSLog(@"json date: %@",epochTime);
	
	NSTimeInterval seconds = [epochTime doubleValue]/1000.0;
	NSDate *epochNSDate = [[NSDate alloc] initWithTimeIntervalSince1970:seconds];
	
	NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
	[dateFormat setDateFormat:@"dd/MM/yyyy hh:mma"];
	
	NSString *dateStr = [dateFormat stringFromDate:epochNSDate];
	NSLog(@"datestring=%@",dateStr);
	
	[dateFormat release];
	[epochNSDate release];
	
	return dateStr;
}
										  

#pragma mark -
#pragma mark Memory management


- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
}


- (void)viewDidUnload {
	self.list = nil;
	[childController release], childController = nil;
}


- (void)dealloc {
	[list release];
	[childController release];
    [super dealloc];
}


@end

