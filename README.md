# Net-Group summer application task

Backend: Spring boot

Frontend: React

Task: Family tree

## When modifying database

### Business rules

* Person is half sibling when they share 1 same parent
* Person is sibling when they share same parents
* Person is mother/father when she/he is a female/male and has the given child
* Person is grandmother/grandfather when she/he is a female/male and has the given child in any of her/his child's children
* Person is blood related when he/she is in the persons family tree with less that 3 links away. Genes are 12.5% the same
* Person is distantly blood related when he/she is in the persons family tree with less that 10 links away. Genes are 0.1% the same
* Person is an ancestor when he/she is upwards in the persons vertical tree
* Every relation between people must be bidirectional and will be created that way by default.
* When giving relationships. All relationships must be redefined. Otherwise there will be dangling connections.
* Family is at least one parent and children of the given parents

### Validation rules

* Person can not be cut. (Person is cut when he took part in graph to tree modification)
* Persons birth date can not be in the future
* Persons death date can not be after death date
* Persons birth date can not be before parents birth date. (One can't be his/her parent nor child)
* Persons country code must be in ALPHA-3 format, matching regex "^[A-Z]{3}$" and can not be NULL
* Persons id code must be in correct format, matching regex "^[0-9]{8,20}$" and can not be NULL
* Persons name must be in correct format, matching regex "^[A-Z][-a-z]+( [A-Z][-a-z]+)+$" and be at least 5 characters long and can not be NULL
* Person must have a gender. It can be undisclosed, but it must be marked so then.
