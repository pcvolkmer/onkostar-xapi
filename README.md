# Example API for Onkostar

This project demonstrates how to create an extended REST API for Onkostar using Spring Web.

## Security implementation

This project implements security aspects for data access based on person pool and form access.
It uses Spring AOP to intercept method calls annotated with custom security annotations and applies authorization checks
accordingly.

The security implementation is based on the existing implementation
in https://github.com/pcvolkmer/onkostar-plugin-dnpm.