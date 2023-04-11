import React from 'react';
import Navbar from 'react-bootstrap/Navbar'
import Container from 'react-bootstrap/Container';
import './Navbar.css'


function NavbarCustom() {
    return (
        <div>
            <Navbar bg="dark" variant="dark">
                <Container>
                    <Navbar.Brand href="#home">*Inserte imagen*</Navbar.Brand>
                    <Navbar.Toggle />
                    <Navbar.Text href="#home" className='navbar-title'>Milk Co</Navbar.Text>
                    
                </Container>
            </Navbar>
        </div>
    )
}


export default NavbarCustom