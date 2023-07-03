import { Component, Input, SimpleChanges } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { TweetService } from 'app/services/tweet.service';
import { ChartOptions, ChartType, ChartDataset, elements } from 'chart.js';

@Component({
  selector: 'app-bar-chart',
  template: './chart.component.html',
  styleUrls: ['./chart.component.css']
})
export class ChartComponent {
  public barChartOptions: ChartOptions = {
    responsive: true, 
    maintainAspectRatio: false,
    scales: {
      x: {
        grid: {
          display: false
        },
        beginAtZero: true
      },
      y: {
        grid: {
          display: true
        },
          beginAtZero: true 

      }
    },
    plugins: {
      legend: {
        display: false 
      }
    },
    elements: {
      bar: {
        backgroundColor: ["red", "green", "blue", "orange", "brown"],
        
      }
    }
  };
  
  
  public barChartLegend = false;

  data?: number[]
  tweets?: Tweet[]

  constructor(tweetService: TweetService){
    tweetService.getMostLikedTweets(0, 6).subscribe(
      receivedTweets => this.tweets = receivedTweets
    )
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tweets'] && changes['tweets'].currentValue && this.tweets)  {
      //TODO preparar gráfico
      
      this.barChartData = [
        {
          data: [0],
          barThickness: 'flex',
        }
      ]
    }
  }
  

  public barChartData: ChartDataset[] = [];

  chartClicked(e: any): void {
    // Handle chart click event
  }

  chartHovered(e: any): void {
    // Handle chart hover event
  }
}
