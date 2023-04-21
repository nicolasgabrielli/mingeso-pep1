import React from "react";
import { Button, Container, Row, Col } from "react-bootstrap";


function Home() {
    return (
        <div className="App">
            <header className="App-header">

                <Container>
                    <Row>
                        <Col>
                            <Button variant="dark" href="/suppliers">
                                Acceso Datos Proveedores
                            </Button>
                        </Col>

                        <Col>
                            <Button variant="dark">
                                Ingresar Productos
                            </Button>
                        </Col>
                    </Row>
                </Container>
            </header>

        </div>
    );
}

export default Home;