This project aims to explore the concepts of Event Sourcing, CQRS and DDD using a real-world domain. 

It has been partially inspired by the [Microsoft CQRS Journey](https://msdn.microsoft.com/en-us/library/jj554200.aspx) in that we want to build a Conference Management System based on their initial functional needs (see below). This domain may be extended in the future.

Please note that the implementation and the design are not related to the Microsoft's one.

# Functional needs #
## Overview of the system ##
Extract from the [Microsoft CQRS Journey](https://msdn.microsoft.com/en-us/library/jj591578.aspx#sec3):

> Contoso plans to build an online conference management system that will enable its customers to plan and manage conferences that are held at a physical location. The system will enable Contoso's customers to:
> 
> * Manage the sale of different seat types for the conference.
> * Create a conference and define characteristics of that conference.
> 
> Business customers will need to register with the system before they can create and manage their conferences.

## Selling seats for a conference ##
Extract from the [Microsoft CQRS Journey](https://msdn.microsoft.com/en-us/library/jj591578.aspx#sec3):
> The business customer defines the number of seats available for the conference. The business customer may also specify events at a conference such as workshops, receptions, and premium sessions for which attendees must have a separate ticket. The business customer also defines how many seats are available for these events.

> The system will require that the names of the attendees be associated with the purchased seats so that an on-site system can print badges for the attendees when they arrive at the conference.

## Creating a conference ##
Extract from the [Microsoft CQRS Journey](https://msdn.microsoft.com/en-us/library/jj591578.aspx#sec3):
> A business customer can create new conferences and manage information about the conference such as its name, description, and dates. The business customer can also make a conference visible on the Conference Management System website by publishing it, or hide it by unpublishing it. 

> Additionally, the business customer defines the seat types and available quantity of each seat type for the conference.
