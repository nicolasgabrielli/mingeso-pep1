import logo from './logo.svg';
import './App.css';

import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import NavbarCustom from './components/Navbar'

function App() {
  return (
    <div className="App">
      <NavbarCustom fixed="top"/>
      <header className="App-header">
        <Container>
          <Row>
            <Col>
              <Button variant="dark">
                Listar Datos de los Proveedores
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

export default App;
