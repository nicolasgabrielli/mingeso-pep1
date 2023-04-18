import React, { Component } from "react";
import { Button, Row } from "react-bootstrap";
import Table from 'react-bootstrap/Table';

class Suppliers extends Component {
    constructor(props) {
        super(props);
        this.state = {
            suppliers: []   // This is the array that will hold the suppliers
        }
    }

    async componentDidMount() {
        const response = await fetch("/suppliers");
        const body = await response.json();
        console.log(body);
        this.setState({ suppliers: body });
        
    }

    render() {
        const { suppliers } = this.state;
        return (
            <div className="App">
                <header className="App-header">

                    <Table striped bordered hover>
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Nombre</th>
                                <th>Código</th>
                                <th>Categoría</th>
                                <th>Retención</th>
                            </tr>
                        </thead>
                        <tbody>
                            {suppliers.map(supplier =>
                                <tr key={supplier.id}>
                                    <td>{supplier.id}</td>
                                    <td>{supplier.name}</td>
                                    <td>{supplier.code}</td>
                                    <td>{supplier.category}</td>
                                    <td>{supplier.retention}</td>
                                </tr>
                            )}
                        </tbody>
                    </Table>
                    <Button href="/suppliers/create">Ingresar Proveedor</Button>
                </header>
            </div>
        );

    }


}

export default Suppliers;