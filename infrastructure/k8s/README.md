# Selenium Grid Kubernetes Deployment

## Prerequisites
- Kubernetes cluster (1.19+)
- Helm 3.x
- kubectl configured

## Quick Start

### 1. Add Selenium Helm Repository
```bash
helm repo add selenium https://www.selenium.dev/docker-selenium
helm repo update
```

### 2. Deploy Selenium Grid
```bash
helm install selenium-grid selenium/selenium-grid \
  -f selenium-grid-values.yaml \
  --namespace selenium \
  --create-namespace
```

### 3. Access Grid
```bash
# Port forward to access Grid UI
kubectl port-forward svc/selenium-grid-selenium-hub 4444:4444 -n selenium

# Grid UI: http://localhost:4444/ui
```

### 4. Run Tests Against Kubernetes Grid
```bash
mvn verify -DgridUrl=http://localhost:4444
```

## Scaling
```bash
# Scale Chrome nodes
kubectl scale deployment selenium-grid-chrome --replicas=5 -n selenium

# Or use autoscaling (KEDA required)
```

## Cleanup
```bash
helm uninstall selenium-grid -n selenium
```
