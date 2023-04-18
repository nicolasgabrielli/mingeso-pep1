import React, { Component } from "react";
import { Button, Col, Container, Form, FormSelect, Row } from "react-bootstrap";

class SupplierCreate extends Component {

    emptySupplier = {                                   // This is the empty supplier
        name: '',
        code: '',
        category: '',
        retention: 'Sí'
    };

    constructor(props) {                                // This is the constructor
        super(props);
        this.state = {
            supplier: this.emptySupplier
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }
    async handleSubmit(event) {                         // This is the function that will handle the submit event
        event.preventDefault();
        const { supplier } = this.state;
        console.log(JSON.stringify(supplier));
        await fetch('/suppliers/create', {          // This is the fetch that will send the supplier to the backend
            method: 'POST',
            body: JSON.stringify(supplier),         // This is the body that will be sent to the backend
            headers: {"Content-type": "application/json"}
        }).then(response => response.json())
        .then(json => console.log(json));
        this.props.history.push('/suppliers');     // This is the redirect to the suppliers list
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let supplier = { ...this.state.supplier };
        supplier[name] = value;
        this.setState({ supplier });
    }

    render() {
        const { supplier } = this.state;
        return (
            <div>
                <Container>
                    <Form onSubmit={this.handleSubmit}>
                        <Row fluid>
                            <Col>
                                <Form.Group className="mb-3" controlId="name">
                                    <Form.Label>Nombre</Form.Label>
                                    <Form.Control required type="text" name="name" value={supplier.name} onChange={this.handleChange} />
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <Form.Group className="mb-3" controlId="code">
                                    <Form.Label>Código</Form.Label>
                                    <Form.Control required type="text" name="code" value={supplier.code} onChange={this.handleChange} />
                                </Form.Group>
                            </Col>
                            <Col>
                                <Form.Group className="mb-3" controlId="category">
                                    <Form.Label>Categoría</Form.Label>
                                    <Form.Control required type="text" name="category" value={supplier.category} onChange={this.handleChange} />
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <Form.Group className="mb-3" controlId="retention">
                                    <Form.Label>Retención</Form.Label>
                                    <FormSelect name="retention" value={supplier.retention} onChange={this.handleChange}>
                                        <option value="Sí">Sí</option>
                                        <option value="No">No</option>
                                    </FormSelect>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <Button variant="primary" type="submit">
                                        Ingresar Proveedor
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Container>
            </div>
        );
    }

}

export default SupplierCreate;