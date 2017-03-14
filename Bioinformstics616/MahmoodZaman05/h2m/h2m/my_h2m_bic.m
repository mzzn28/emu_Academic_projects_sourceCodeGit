
% my_h2m_bic: EM mixture model using h2m implementation
% Syntax: BIC = my_h2m_bic(data, nc_max, disp)
%          data - data matrix
%        mc_max - max # of each class
%          disp - display (0/1)
%           BIC - BIC list
% Example: bic = my_h2m_bic(iris, 5);
% Date: 1/29/2003

function BIC = my_h2m_bic(data, nc_max, disp)

if nargin < 3
    disp = 0;
end

[m n] = size(data);

% Constant: Flag for full covariance matrices
% p=n; N=NC; X=data; T=m;
FULL_COV = 0;

% Values of the BIC criterion
BIC = zeros(1, nc_max);

% Number of EM iterations
NIT = 10;

% NC = 1 corresponds to a gaussian model
NC = 1;
w = 1;
mu = mean(data);
Sigma = cov(data);
logl = sum(gauslogv(data, mu, Sigma,1));
BIC(NC) = 2*logl-(n*NC+n*NC+NC-1)*log(m);

% Plot data fit

% Calculate subplot numbers
pcol = ceil(sqrt(nc_max+1));
prow = ceil((nc_max+1) / pcol) + 1;


if disp == 1
    % Data points
    subplot(prow, pcol, 1);
    set(gca, 'fontsize', 8);
    plot(data(:,1), data(:,2), 'b.');
    va = axis;

    % Gausian elipsoid with 50% of the data
    subplot(prow, pcol, 2);
    %title(['BIC = ' sprintf('%.0f', BIC(NC))]);
    set(gca, 'fontsize', 8);
    hold on
    plot(data(:,1), data(:,2), 'b.');
    gauselps(mu,Sigma,'r-',0.5);
    hold off
    axis(va);
    %axis([-2 2 -2 2]);
    drawnow;
end

%NC = 2;
for NC = 2:nc_max
  [tmp, ind] = max(w);
  % Find main axis
  [V, D] = eig(Sigma(1+(ind-1)*n:ind*n,:));
  [tmp, ind2] = max(diag(D));
  
  % Duplicate one component
  mu = [mu(1:ind-1,:); mu(ind,:)-sqrt(D(ind2,ind2))*V(:,ind2)'; ...
	mu(ind,:)+sqrt(D(ind2,ind2))*V(:,ind2)'; mu(ind+1:NC-1,:)];
  Sigma = [Sigma(1:ind*n,:); Sigma(1+(ind-1)*n:(NC-1)*n,:)];
  
  % EM iterations
  w = [w(1:ind-1) w(ind)/2 w(ind)/2 w(ind+1:NC-1)];
  
  % Compute log-likelihood
  [w, mu, Sigma, logl] = mix(data, w, mu, Sigma, NIT);
  [gamma, logl] = mix_post(data, w, mu, Sigma, 1);
  
  % and BIC criterion
  BIC(NC) = 2*logl-(n*NC+n*NC+NC-1)*log(m);
  
  % Plot data fit
  if disp == 1
      subplot(prow, pcol, 1+NC);
      set(gca, 'fontsize', 8);
      % Gausian elipsoids with 50% of the data
      %title(['BIC = ' sprintf('%.0f', BIC(NC))]);
      hold on
      plot(data(:,1), data(:,2), 'b.');
      gauselps(mu,Sigma,'r-',0.5);
      hold off
      axis(va);
      drawnow;
  end
end

% When evrything is done report BIC criterion (best fit is given by the
% maximal value)
if disp == 1
    subplot(prow,1,prow);
    set(gca, 'fontsize', 8);
    axis;
    plot(BIC);
    title('BIC');
    set(gca, 'XTick', 1:nc_max);
end