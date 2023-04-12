import React from 'react';
import Navbar from 'react-bootstrap/Navbar'
import Container from 'react-bootstrap/Container';
import './Navbar.css';

function NavbarCustom() {
    return (
        <div>
            <Navbar bg="dark" variant="dark">
                <Container>
                    <Navbar.Brand href="/">
                    <img src={require('../resources/cow.gif')} 
                        width="144"
                        height="96"

                    />
                    </Navbar.Brand>
                    <Navbar.Toggle />
                    <Navbar.Text href="/" className='navbar-title'>MilkStgo</Navbar.Text>
                </Container>
            </Navbar>
        </div>
    )
}


export default NavbarCustom