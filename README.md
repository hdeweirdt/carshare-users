# Car Share

Application for a car-sharing platform.

## Functionalities

* Users can register 
* Users can update their information
* Users can login, logout
* Users can send an application to add a new car to the system.
* Users can make a reservation for a car
* Users can cancel a reservation for a car
* Users can shorten a reservation for a car
* Users can register a finished reservation (number of km ridden + optional fuel fill-up registration)

* Admins can approve an application for a new car.

* The platform can send an quarterly invoice for all the reservations in that quarter.

## Domain model / rules

* A user can have 0 or more cars registered in the system
* A car is tied to 1 user.
* A reservation is always for 1 car
* Reservations cannot overlap
* Cost of a reservation: 30c/km for the first 100 km, 25c/km for all kilometers after the first 100.
* User can only make a reservation if their profile is complete
